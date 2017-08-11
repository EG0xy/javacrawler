// async function dbFuc() {
//     let docs = [1, 2, 3];
//
//     for (let doc of docs) {
//         await new Promise((resolve,reject) => {
//             setTimeout(() => {
//                 throw new Error("error");
//                 console.log(doc + " completed");
//                 resolve(doc);
//             }, 1000);
//         }).catch((err) => console.log(err));
//     }
//     return "completed";
// }
//
// async function run() {
//     let result = await dbFuc();
//     console.log(result);
// }
// run();
//
let desc = "It all started on October, 8th, 1792 when a monk handed Wilhelm Muehlen a miraculous water secret formula. Muehlens created an eau de cologne that was believed to have healing properties. The name of 4711 was given because it was the street number of Muehlens’ house. This fragrance is a classic in the world of perfumery today and also has a whole bath line.";
let result = desc.match(/([1-2][0-9]{3})/igm);
console.log(result);



