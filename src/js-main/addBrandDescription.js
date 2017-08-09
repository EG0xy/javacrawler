//将描述添加进mongo brand表中
const fs = require("fs");

/**
 * 返回描述brand信息的数据
 * @returns {*}
 */
function readBrandDescFile() {
    let files = [
        // 'E:/workspace/brandcrawler/src/main/resources/dermstore.com_brand_有描述_result.txt',
        'E:/workspace/brandcrawler/src/main/resources/sephora.fr_brand_有描述_result.txt',
        'E:/workspace/brandcrawler/src/main/resources/farfetch.com_brand.txt',
    ];
    let map = new Map();
    files.map((file) => fs.readFileSync(file, 'utf8'))
         .map((line) => line.split("\n"))
         .map((el) => el.map((innerEl) => {
             try {
                 if (innerEl.startsWith("{"))
                     return JSON.parse(innerEl);
                 else {
                     let elSplit = innerEl.split("|");
                     return {brandName: elSplit[0], brandNameDescriptionCn: elSplit[elSplit.length - 1]};
                 }
             } catch (e) {
                 console.log(innerEl);
                 return {};
             }
         })).reduce((val, newVal) => {
        return val.concat(newVal)
    }, [])
         .forEach((el) => {
             try {
                 map.set(el.brandName.toUpperCase().trim(), el);
             } catch (e) {
                 console.log(JSON.stringify(el) + "is error ignore")
             }
         });
    return map;

}


async function main() {
    let allBrandsDesc = readBrandDescFile();

    console.log("brand desc count:" + allBrandsDesc.size);
    let MongoClient = require('mongodb').MongoClient;
    let assert = require('assert');

    let url = 'mongodb://10.0.0.84:27017/masterdata';
    let db = await MongoClient.connect(url).catch((err) => console.log(err));
    console.log("Connected correctly to server.");
    let brandCollection = db.collection('md_mt_brand');
    let cursor = brandCollection.find();

    let docs = [], all = await  cursor.count();

    let batch = brandCollection.initializeUnorderedBulkOp();

    await new Promise((resolve) => {
        cursor.forEach(function (doc) {
            let existDoc;
            if ((existDoc = allBrandsDesc.get(doc.brandName))) {
                console.log("update " + existDoc.brandName);
                if (existDoc.brandNameDescriptionCn) {
                    batch.find({"_id": doc._id}).update({
                        $set: {
                            brandNameDescriptionCn: existDoc.brandNameDescriptionCn
                        }
                    });
                } else {
                    batch.find({"_id": doc._id}).update({
                        $set: {
                            brandUrl: existDoc.brandUrl, brandLogoSrc: existDoc.brandLogoSrc,
                            brandDescription: existDoc.brandDescription
                        }
                    });
                }

            }
            docs.push(doc);
            if (docs.length === all) {
                resolve();
            }
        });
    });
    let batchResult = await batch.execute();
    console.log(batchResult);
    db.close();

}

main().then(() => {
    console.log("all done!")
});