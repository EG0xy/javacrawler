// 品牌数据规整
let fs = require("fs");
let _ = require("lodash");
let MongoClient = require('mongodb').MongoClient;

const fileMap = {
    "bloomingdales_brand.txt": (line) => {
        return {name: line.trim(), nameEn: line.trim()}
    },
    "dermstore.com_brand_有描述_result.txt": (line) => {
        let obj = JSON.parse(line);
        return {
            name: obj.brandName,
            nameEn: obj.brandName,
            fromUrl: obj.brandUrl,
            logoUrl: obj.brandLogoSrc,
            descEn: obj.brandDescription,
        }
    },
    "farfetch.com_brand.txt": (line) => {
        let split = line.split("|");
        return {
            name: split[0].trim(),
            fromUrl: split[1],
            nameEn: split[0],
            descEn: split[split.length - 1]
        };
    },
    "galerieslafayette.com_brand.txt": (line) => {
        let name = line.trim();
        return {
            name: name,
            nameEn: name,
        };
    },
    "giltbrand.txt": (line) => {
        let name = line.trim();
        return {
            name: name,
            nameEn: name,
        };
    },
    "neimanmarcus.com_brand.txt": (line) => {
        let split = line.split("|");
        let name = split[0].trim();
        return {
            name: name,
            nameEn: name,
            fromUrl: split[1].trim()
        }
    },
    "nordstorm.com_brand.txt": (line) => {
        let name = line.trim();
        return {
            name: name,
            nameEn: name,
        };
    },
    "overstock_brand.txt": (line) => {
        let name = line.split("|")[0].trim();
        return {
            name: name,
            nameEn: name,
        };
    },
    "sephora.fr_brand_有描述_result.txt": (line) => {
        let obj = JSON.parse(line);
        return {
            name: obj.brandName,
            nameEn: obj.brandName,
            fromUrl: obj.brandUrl,
            logoUrl: obj.brandLogoSrc,
            descEn: obj.brandDescription,
        }
    },
    "springbrandwithdesc.txt": (line) => {
        let split = line.split("|");
        let name = split[0].trim();
        let firstSpace = split[1].indexOf(" ");
        let fromUrl = split[1].substr(0, firstSpace);
        let description = split[1].substr(firstSpace).trim();
        return {
            name: name,
            nameEn: name,
            fromUrl: fromUrl,
            descEn: description
        }
    },
    "cn.shopbop.com_brand.txt": (line) => {
        let name = line.trim();
        return {
            name,
            nameEn: name
        }
    },
    "ln-cc.com_brand.txt": (line) => {
        let split = line.split("|");
        let name = split[0].trim();
        let fromUrl = split[1];
        return {name, nameEn: name, fromUrl}
    },
    "bluefly.com_brand.txt": (line) => {
        let name = line;
        return {name, nameEn: name}
    },
    "net-a-porter.com_brand_result.txt": (line) => {
        let obj = JSON.parse(line);
        return {
            name: obj.brandName,
            nameEn: obj.brandName,
            descCn: obj.brandDescriptionCn,
            fromUrl: obj.brandUrl
        }
    },
    "mytheresa.com_result.txt": (line) => {
        let obj = JSON.parse(line);
        return {
            name: obj.brandName,
            nameEn: obj.brandName,
            fromUrl: obj.brandUrl,
            logoUrl: obj.brandLogoSrc,
            descEn: obj.brandDescription
        }
    },
    "ln-cc.com_brand_result.txt": (line) => {
        let obj = JSON.parse(line);
        return {
            name: obj.brandName,
            nameEn: obj.brandName,
            fromUrl: obj.brandUrl,
            descEn: obj.descEn
        }
    },
    "cn.shopbop.com_brand_result.txt": (line) => {
        let obj = JSON.parse(line);
        return {
            name: obj.brandName,
            nameEn: obj.brandName,
            fromUrl: obj.brandUrl,
            descEn: obj.brandDescription
        }
    },
    "antonioli.eu_brand_result.txt": (line) => {
        let obj = JSON.parse(line);
        return {
            name: obj.brandName,
            nameEn: obj.brandName,
            fromUrl: obj.brandUrl,
            descEn: obj.brandDescription
        }
    },
    "luisaviaroma.com_brand_result.txt": (line) => {
        let obj = JSON.parse(line);
        return {
            name: obj.brandName,
            nameEn: obj.brandName,
            fromUrl: obj.brandUrl,
            descEn: obj.brandDescription,
            descCn: obj.brandDescriptionCn
        }
    },
    "perfumesclub.co.uk_brand_result.txt": (line) => {
        let obj = JSON.parse(line);
        return {
            name: obj.brandName,
            nameEn: obj.brandName,
            fromUrl: obj.brandUrl,
            descEn: obj.brandDescription
        }
    }
};

let allLineCount = 0;
let hasYearCount = 0;
let notValidCount = 0; //既没有logo也没有descEn也没有descCn的brand数量
let validCount = 0;
let mongoUrl = 'mongodb://10.0.0.84:27017/masterdata';


function run() {
    /**
     * @return {string}
     */
    function KeyFn(name) {
        return name.toUpperCase().trim();
    }

    let map = Object.keys(fileMap).map((file) => {
        let fileData = fs.readFileSync('E:/workspace/brandcrawler/src/main/resources/' + file, 'utf8');
        return {
            fileName: file,
            data: fileData.split("\n").filter((line) => line.length > 0)
        }
    }).map((fileObj) => {
        let objArr = fileObj.data.map((el) => {
            try {
                let obj = fileMap[fileObj.fileName](el);
                for (let prop in obj) {
                    if (_.isEmpty(obj[prop]) || obj[prop] === "undefined") {
                        delete obj[prop];
                    }
                    // propertyName is what you want
                    // you can get the value like this: myObject[propertyName]
                }
                return obj;
            } catch (e) {
                console.log("error->" + el, e);
                return null;
            }
        }).filter((obj) => !_.isEmpty(obj));
        allLineCount += objArr.length;
        console.log(fileObj.fileName + "读取行:" + objArr.length);
        return objArr;
    }).reduce((map, objArr) => {
        objArr.forEach((obj) => {
            let key = KeyFn(obj.name);
            let oldObj = map.get(key) || {};
            //定制下,取长度比较长的描述
            let newObj = _.assignInWith({}, oldObj, obj, (oldVal, newVal) => {
                oldVal = oldVal || "";
                newVal = newVal || "";
                return oldVal.length < newVal.length ? newVal : oldVal;
            });
            map.set(key, newObj);
        });
        return map;
    }, new Map());
    console.log("品牌合计:" + map.size);
    checkLocal(map);
    writeToFile(map);
}
function checkLocal(map) {
    let localData = fs.readFileSync('E:/workspace/brandcrawler/src/main/resources/local_brand.txt', 'utf8');
    let found = 0;
    let brands = new Set(localData.split("\n").map((el) => el.trim().toUpperCase()));
    brands.forEach((el) => {
        if (map.has(el)) {
            found++;
        } else {
            // console.log(el);
        }
    });

    console.log("所有文件合计行:" + allLineCount);
    console.log("完全匹配 found/all=" + found + "/" + brands.size + " " + (parseInt(found * 100 / brands.size)) + "/100");

    //移除非字母数字空格之后再比较(首单词相同我们当做相同的)
    let mapFn = (el) => el.replace(/[^a-z0-9 ]/gmi, "").split(" ")[0];
    brands = new Set([...brands.keys()].map(mapFn));
    let standardBrands = new Set([...map.keys()].map(mapFn));
    let sameCount = 0, commonPrefixCount = 0;

    [...brands.keys()].forEach((el) => {
        if (standardBrands.has(el)) {
            sameCount++;
        }
        // else {
        //     for (let sel of standardBrands) {
        //         if (commonPrefix(sel, el) >= 4) {
        //             commonPrefixCount++;
        //             console.log(`${sel},${el}`);
        //             break;
        //         }
        //     }
        // }
    });
    console.log("模糊匹配 found/all=" + (sameCount + commonPrefixCount) + "/" + brands.size + " " + (parseInt((sameCount + commonPrefixCount) * 100 / brands.size)) + "/100");
}

function commonPrefix(a, b) {
    let i = 0,
        l = Math.min(a.length, b.length);

    while (i < l && a[i] === b[i]) {
        i++;
    }
    return i;
}

async function writeToFile(map) {
    let created = new Date();
    let defaultBrandObj = {
        name: null,
        nameEn: null,   //英文名称
        nameCn: null,   //中文名称
        parentId: null, //父ID
        aliasNames: [], //别名
        logoUrl: null,  //logo的url
        brandParentId: 0, //父品牌Id
        descEn: null, //英文描述
        descCn: null, //中文描述
        imageUrls: [],   //品牌图片
        platforms:	   //平台品牌属性
            {
                TM: {},
                JUMEI: {},
                JD: {}
            },
        birthPlace: null,   //产地
        birthYear: null,
        fromUrl: null,  //品牌来源url
        active: true,
        created: created,
        creater: "SYSTEM",
        modified: created,
        modifier: "SYSTEM"
    };

    let stream = fs.createWriteStream("E:/workspace/brandcrawler/src/main/resources/result/branddata_regular.json",
        {'flags': 'w'});

    let str = "[\n";

    let mongo_records = [];
    [...map.entries()].sort((entry) => entry[0].toUpperCase()).forEach((entry, brandId) => {
        let brandObj = Object.assign({brandId}, defaultBrandObj, entry[1]);

        if (brandObj.descEn || brandObj.descCn) {
            let desc = brandObj.descEn || brandObj.descCn;
            let matches = desc.match(/([1-2][0-9]{3})/igm) || [];
            if (matches.length) {
                hasYearCount++;
                brandObj.birthYear = matches[0];
            }
        }
        if (_.isEmpty(brandObj.logoUrl) && _.isEmpty(brandObj.descCn)
            && _.isEmpty(brandObj.descEn)) {
            notValidCount++;
        } else {
            validCount++;
        }
        // str += brandObj.name + "\n";
        str += JSON.stringify(brandObj) + ",\n";
        mongo_records.push(brandObj);
    });
    str += "]\n";
    stream.write(str);
    stream.end();
    stream.close();
    console.log(`含有年份的品牌数量:${hasYearCount}`);
    console.log(`不规整的品牌数量 ${notValidCount},规整的品牌数量 ${validCount}`);
    let db= await MongoClient.connect(mongoUrl).catch((err)=>{console.log(`connect to mongo error,${err}`)});
    await db.collection('md_mt_brand').drop().catch(()=>{});
    await db.collection('md_mt_brand').insertMany(mongo_records);
    console.log(`add mongo completed!`);
    process.exit(0);
}
run();








