async function dbFuc() {
    let docs = [1, 2, 3];

    for (let doc of docs) {
        await new Promise((resolve,reject) => {
            setTimeout(() => {
                throw new Error("error");
                console.log(doc + " completed");
                resolve(doc);
            }, 1000);
        }).catch((err) => console.log(err));
    }
    return "completed";
}

async function run() {
    let result = await dbFuc();
    console.log(result);
}
run();






