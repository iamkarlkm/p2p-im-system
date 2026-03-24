const { chromium } = require('playwright');

async function main() {
  const browser = await chromium.launch({ headless: false });
  const page = await browser.newPage();
  
  console.log('浏览器已打开，按 Ctrl+C 关闭');
  
  // 保持浏览器开启
  await page.goto('https://www.bing.com');
  
  // 等待用户关闭
  await new Promise(() => {});
}

main().catch(console.error);
