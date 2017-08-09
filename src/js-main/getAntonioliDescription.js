/**
 *
 * @author holysky.zhao 2017/8/2 10:54
 * @version 1.0.0
 */
const CDP = require("chrome-remote-interface");
const $ = require('cheerio');
const fs = require("fs");

CDP(async chrome => {
    chrome.Page.enable();
    let stream = fs.createWriteStream("E:/workspace/brandcrawler/src/main/resources/antonioli.eu_brand_result.txt",
        { 'flags': 'a'});

    function sequence(tasks, fn) {
        return tasks.reduce((promise, task) => promise.then((data) => fn(task, data)), Promise.resolve([]));
    }

    function getBrands(line, result) {
        return new Promise((resolve) => {
            let split = line.split("|");
            let dataObj = {
                brandName: split[0],
                brandUrl: split[1]
            };
            chrome.Page.navigate({url: dataObj.brandUrl})
                  .then(() => {
                      chrome.once('Page.loadEventFired', () => {
                          chrome.DOM.getDocument().then((node) => {
                              return chrome.DOM.getOuterHTML({nodeId: node.root.nodeId});
                          }).then((resp) => {
                              let root = $.load(resp.outerHTML);
                              dataObj.brandDescription = root("aside.filters header p").text();
                              let message = JSON.stringify(dataObj);
                              console.log(message);
                              stream.write(message+"\n");
                              result.push(dataObj);
                              resolve(result);
                          });
                      });
                  });
        });
    }
    let fileData = fs.readFileSync('E:/workspace/brandcrawler/src/main/resources/antonioli.eu_brand.txt', 'utf8');

    sequence(fileData.split("\n").filter((el)=>!el.startsWith("#")), getBrands).then((data) => {
        stream.end();
        console.log("all done");
        chrome.close();
    })
}).on("error", err => {
    // cannot connect to the remote endpoint
    console.error(err);
});


