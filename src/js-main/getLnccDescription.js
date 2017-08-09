/**
 *
 * @author holysky.zhao 2017/8/2 10:54
 * @version 1.0.0
 */
const CDP = require("chrome-remote-interface");
const $ = require('cheerio');
const fs = require("fs");

const READ_FILE = 'E:/workspace/brandcrawler/src/main/resources/ln-cc.com_brand.txt';
const WRITE_FILE = "E:/workspace/brandcrawler/src/main/resources/ln-cc.com_brand_result.txt";


function fillObj(dataObj, root) {
    dataObj.brandDescription = root("p.plp-rendering-brands-text").text().trim();
    return dataObj;
}
CDP(async chrome => {
    chrome.Page.enable();
    let stream = fs.createWriteStream(WRITE_FILE,
        { 'flags': 'w'});

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
                              dataObj=fillObj(dataObj, root);
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


