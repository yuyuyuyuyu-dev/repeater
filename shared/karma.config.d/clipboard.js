const fs = require("fs");
const os = require("os");
const path = require("path");

const userDataDir = fs.mkdtempSync(path.join(os.tmpdir(), "repeater-chrome-"));
fs.mkdirSync(path.join(userDataDir, "Default"));
fs.writeFileSync(
  path.join(userDataDir, "Default", "Preferences"),
  JSON.stringify({
    profile: {
      content_settings: {
        exceptions: {
          clipboard: { "localhost,*": { setting: 1 } },
        },
      },
    },
  }),
);
process.on("exit", () => fs.rmSync(userDataDir, { recursive: true, force: true }));

config.set({
  browsers: ["ChromeHeadlessWithClipboard"],
  customLaunchers: {
    ChromeHeadlessWithClipboard: {
      base: "ChromeHeadless",
      chromeDataDir: userDataDir,
    },
  },
  client: {
    ...config.client,
    useIframe: false,
  },
});
