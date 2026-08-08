# repeater

入力された文字を繰り返します。

https://repeater.yuyuyuyuyu.dev

![repeaterのスクリーンショット](screenshot.png)

## 構成

Kotlin Multiplatform / Compose Multiplatform で作られたWebアプリです。

- [shared](./shared/src) — 画面と`repeat`のロジック。全ターゲットで共有します。
  - [commonMain](./shared/src/commonMain/kotlin) — 全ターゲット共通のコード。
  - [webMain](./shared/src/webMain/kotlin) / [jvmMain](./shared/src/jvmMain/kotlin) — クリップボードなど、ターゲット固有の実装。
- [webApp](./webApp/src) — Webアプリのエントリポイント。`index.html`やPWAのアセットもここにあります。
- [desktopApp](./desktopApp/src) — デスクトップ(JVM)アプリのエントリポイント。ローカルでのデバッグをしやすくするためのもので、配布はしません。

## 実行

- Webアプリ
  - Wasmターゲット（高速・モダンブラウザ向け）: `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - JSターゲット（低速・古いブラウザにも対応）: `./gradlew :webApp:jsBrowserDevelopmentRun`
- デスクトップアプリ
  - ホットリロード: `./gradlew :desktopApp:hotRun --auto`
  - 通常実行: `./gradlew :desktopApp:run`

## テスト

- デスクトップ: `./gradlew :shared:jvmTest`
- Web
  - Wasmターゲット: `./gradlew :shared:wasmJsTest`
  - JSターゲット: `./gradlew :shared:jsTest`

## デプロイ

`main`ブランチへのpushで、[GitHub Actions](./.github/workflows/pages.yml)がWasmターゲットのビルド結果をGitHub Pagesへデプロイします。

PWA化は[ComposePWA](https://github.com/yuyuyuyuyu-dev/ComposePWA)が担っていて、`manifest.json`やService Workerは`wasmJsBrowserDistribution`／`jsBrowserDistribution`の実行時に生成・更新されます。

## ライセンス

本体は[MIT License](./LICENSE)です。

同梱しているフォント[Yomogi](https://fonts.google.com/specimen/Yomogi)はSIL Open Font License 1.1で提供されています（[ライセンス全文](./licenses/Yomogi-OFL.txt)）。
