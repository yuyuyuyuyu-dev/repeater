const fs = require("fs/promises");
const path = require("path");
const { setTimeout: sleep } = require("timers/promises");
const ChromeHeadlessBrowser = require("karma-chrome-launcher")["launcher:ChromeHeadless"][1];

function ChromeHeadlessWithClipboard(baseBrowserDecorator, args) {
  ChromeHeadlessBrowser.call(this, baseBrowserDecorator, {
    ...args,
    flags: [...(args.flags ?? []), "--remote-debugging-port=0"],
  });

  const getOptions = this._getOptions;
  this._getOptions = (url) => {
    openWithClipboardGranted(this._tempDir, url).catch((error) => {
      console.error("Could not open the tests with clipboard access granted", error);
    });
    return getOptions.call(this, "about:blank");
  };
}

ChromeHeadlessWithClipboard.prototype = {
  ...ChromeHeadlessBrowser.prototype,
  name: "ChromeHeadlessWithClipboard",
};
ChromeHeadlessWithClipboard.$inject = ["baseBrowserDecorator", "args"];

async function openWithClipboardGranted(userDataDir, url) {
  const [port, browserPath] = await readDevToolsActivePort(userDataDir);
  const connectionKeptOpenToKeepTheGrant = await connectToDevTools(
    `ws://127.0.0.1:${port}${browserPath}`,
  );
  await connectionKeptOpenToKeepTheGrant.send("Browser.grantPermissions", {
    origin: new URL(url).origin,
    permissions: ["clipboardReadWrite", "clipboardSanitizedWrite"],
  });
  const { targetInfos } = await connectionKeptOpenToKeepTheGrant.send("Target.getTargets");
  const page = targetInfos.find((target) => target.type === "page");
  const { sessionId } = await connectionKeptOpenToKeepTheGrant.send("Target.attachToTarget", {
    targetId: page.targetId,
    flatten: true,
  });
  await connectionKeptOpenToKeepTheGrant.send("Page.navigate", { url }, sessionId);
}

async function readDevToolsActivePort(userDataDir) {
  const file = path.join(userDataDir, "DevToolsActivePort");
  for (;;) {
    const lines = await fs.readFile(file, "utf8").then(
      (content) => content.split("\n"),
      () => [],
    );
    if (lines.length >= 2 && lines[1] !== "") {
      return lines;
    }
    await sleep(50);
  }
}

function connectToDevTools(endpoint) {
  return new Promise((resolve, reject) => {
    const socket = new WebSocket(endpoint);
    const pending = new Map();
    let nextId = 0;

    socket.addEventListener("message", ({ data }) => {
      const message = JSON.parse(data);
      const request = pending.get(message.id);
      if (request === undefined) {
        return;
      }
      pending.delete(message.id);
      if (message.error) {
        request.reject(new Error(`${request.method}: ${message.error.message}`));
      } else {
        request.resolve(message.result);
      }
    });
    socket.addEventListener("error", reject, { once: true });
    socket.addEventListener(
      "open",
      () =>
        resolve({
          send: (method, params, sessionId) =>
            new Promise((resolveRequest, rejectRequest) => {
              const id = ++nextId;
              pending.set(id, { method, resolve: resolveRequest, reject: rejectRequest });
              socket.send(JSON.stringify({ id, method, params, sessionId }));
            }),
        }),
      { once: true },
    );
  });
}

config.plugins.push({
  "launcher:ChromeHeadlessWithClipboard": ["type", ChromeHeadlessWithClipboard],
});

config.set({
  browsers: ["ChromeHeadlessWithClipboard"],
  client: {
    ...config.client,
    useIframe: false,
  },
});
