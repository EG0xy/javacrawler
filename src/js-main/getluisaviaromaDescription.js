/**
 *
 * @author holysky.zhao 2017/8/2 10:54
 * @version 1.0.0
 */
const CDP = require("chrome-remote-interface");
const $ = require('cheerio');
const fs = require("fs");

const READ_FILE = 'E:/workspace/brandcrawler/src/main/resources/luisaviaroma.com_brand.txt';
const WRITE_FILE = "E:/workspace/brandcrawler/src/main/resources/luisaviaroma.com_brand_result.txt";

//transfer data
// let fileData = fs.readFileSync(READ_FILE, 'utf8');
// let map = new Map();
// fileData.split("\n").forEach((el) => {
//     let split = el.split("|").filter((line)=>line.length>0);
//     let obj = map.get(split[0])||{};
//     try {
//         if (split[1].indexOf("%") != -1) {
//             obj.zh = split[1];
//         } else {
//             obj.en = split[1];
//         }
//     } catch (e) {
//         console.log("error line"+el)
//     }
//     map.set(split[0], obj);
// });
//
// map.forEach((obj,key) => {
//     console.log(key + "|" +obj.en + "|" + obj.zh);
// });




CDP(async chrome => {
    chrome.Page.enable();
    let stream = fs.createWriteStream(WRITE_FILE,
        { 'flags': 'w'});

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

        let urls = [ split[1]];
        if(split[2].length>0) {
            urls.push(split[2])
        }
        return new Promise(async (resolve) => {
            let descs=[];
            for(let url of urls) {
                await chrome.Page.navigate({url})
                      .then(()=>chrome.Page.loadEventFired())
                      .then(()=>chrome.DOM.getDocument())
                      .then((node) =>chrome.DOM.getOuterHTML({nodeId: node.root.nodeId}))
                      .then((resp) => {
                          let root = $.load(resp.outerHTML);
                          let desc = root("p#div_des_banner_txt").text().trim();
                          let image = "https://www.luisaviaroma.com" + root("#div_des_banner_image img").attr("src");
                          descs.push({ desc, image});
                      }).catch((err)=>{
                        console.log("error url:"+url)
                    });
            }
            dataObj.brandDescription = descs[0].desc;
            if(descs.length==2) {
                dataObj.brandDescriptionCn = descs[1].desc;
            }
            dataObj.image = descs[0].image;
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


