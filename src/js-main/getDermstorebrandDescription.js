/**
 *
 * @author holysky.zhao 2017/8/2 10:54
 * @version 1.0.0
 */
const CDP = require("chrome-remote-interface");
const $ = require('cheerio');
const fs = require("fs");
const _ = require("lodash");


let loading=true; //全局变量,每次loadEventFired调用时设为false,

CDP(chrome => {
    chrome.Page.enable();
    let stream = fs.createWriteStream("E:/workspace/brandcrawler/src/main/resources/dermstore.com_brand_有描述_result.txt");
    // stream.once('open', function(fd) {
    //     stream.write("My first row\n");
    //     stream.write("My second row\n");
    //     stream.end();
    // });


    function partition(items, size) {
        let result = _.groupBy(items, function(item, i) {
            return Math.floor(i/size);
        });
        return _.values(result);
    }

    fs.readFile('E:/workspace/brandcrawler/src/main/resources/dermstore.com_brand_有描述.txt', 'utf8', function (err, data) {
        if (err) {
            return console.log(err);
        }
        async function  sequence(tasks, fn) {
            return tasks.reduce((promise, task) => promise.then((data) => fn(task, data)), Promise.resolve([]));
        }

        async function getBrands(line, data) {

            console.log(data);
            return line;
            // if(line.startsWith("###")) return Promise.resolve(data);


            // return new Promise((resolve) => {
            //     let split = line.split("|");
            //     let category = split[0];
            //     let url = split[1];
            //     try {
            //         loading=true; //开始loading
            //         chrome.Page.navigate({url: url})
            //               .then(()=>new Promise((resolve)=>{
            //                   chrome.once('Page.loadEventFired', ()=>{
            //                       console.log(url+"complete");
            //                       resolve();
            //                   });
            //                   setTimeout(resolve, 30000); //30秒强制结束
            //               }))
            //               .then(() => {
            //                   chrome.DOM.getDocument().then((node) => {
            //                       return chrome.DOM.getOuterHTML({nodeId: node.root.nodeId});
            //                   }).then((resp) => {
            //                       let root = $.load(resp.outerHTML);
            //                       let text = root("#brands span.refinement-text").text();
            //                       let brands = root("#brands span.refinement-text").map((i, el) => {
            //                           try {
            //                               return el.children[1].data;
            //                           } catch (e) {
            //                               return "";
            //                           }
            //                       }).filter((el) => el !== "");
            //
            //                       console.log(line + " 含有品牌" + brands.length);
            //                       brands.each((i, el) => {
            //                           data.set(el, category);
            //                           stream.write(el + "|" + category + "\n");
            //                       });
            //                       resolve(data)
            //                   })
            //               });
            //
            //     } catch (e) {
            //         console.log(line + " 品牌读取失败");
            //         resolve(data);
            //     }
            //
            // })
        }

        let lines = data.split("\n");
        let linePartition = partition(lines, 10);

        let data= await sequence(linePartition, getBrands);

        data.forEach((key, val) => {

        });
        stream.end();
        console.log("all done");
        chrome.close();
        return await sequence()

    });
}).on("error", err => {
    // cannot connect to the remote endpoint
    console.error(err);
});


