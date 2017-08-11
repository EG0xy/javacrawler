/**
 *
 * @author holysky.zhao 2017/8/2 10:54
 * @version 1.0.0
 */
const CDP = require("chrome-remote-interface");
const $ = require('cheerio');
const fs = require("fs");

const READ_FILE = 'E:/workspace/brandcrawler/src/main/resources/perfumesclub.co.uk_brand.txt';
const WRITE_FILE = "E:/workspace/brandcrawler/src/main/resources/perfumesclub.co.uk_brand_result.txt";

CDP(async chrome => {
    chrome.Page.enable();
    let stream = fs.createWriteStream(WRITE_FILE,
        { 'flags': 'a'});

    function sequence(tasks, fn) {
        return tasks.reduce((promise, task) => promise.then((data) => fn(task, data)), Promise.resolve([]));
    }
    // /cn/zh
    function getBrands(line, result) {
        let split = line.split("|");
        let dataObj = {
            brandName: split[0],
            brandUrl: split[1]
        };
        let urls = [split[1]];
        return new Promise(async (resolve) => {
            let descs=[];
            for(let url of urls) {
                await chrome.Page.navigate({url})
                      .then(()=>chrome.Page.loadEventFired())
                      .then(()=>chrome.DOM.getDocument())
                      .then((node) =>chrome.DOM.getOuterHTML({nodeId: node.root.nodeId}))
                      .then((resp) => {
                          let root = $.load(resp.outerHTML);
                          let desc = root("div.introFilter").text().trim();
                          desc && descs.push(desc);
                      });
            }
            descs.length>0 &&  (dataObj.brandDescription = descs[0]);
            let message = JSON.stringify(dataObj);
            console.log(message);
            stream.write(message + "\n");
            result.push(dataObj);
            resolve(result);
        });
    }

    let fileData = fs.readFileSync(READ_FILE, 'utf8');

    sequence(fileData.split("\n").filter((el)=>!el.startsWith("#")), getBrands).then((data) => {
        stream.end();
        console.log("all done");
        chrome.close();
    })
}).on("error", err => {
    // cannot connect to the remote endpoint
    console.error(err);
});


