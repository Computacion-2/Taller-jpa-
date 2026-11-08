import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';
import assert from 'node:assert/strict';
import { fileURLToPath } from 'node:url';
import readline from 'node:readline';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const collection = JSON.parse(fs.readFileSync(path.join(root, 'postman/Finanzas.postman_collection.json')));
const environmentFile = process.argv[2] ? path.resolve(process.argv[2]) : path.join(root, 'postman/Local.postman_environment.json');
const environment = new Map(JSON.parse(fs.readFileSync(environmentFile)).values.map(x => [x.key, x.value]));
if (process.argv[3]) environment.set('baseUrl', process.argv[3]);
const bridge = process.argv.includes('--mockmvc')
    ? readline.createInterface({ input: process.stdin }) : null;
const replies = bridge?.[Symbol.asyncIterator]();
async function transport(url, options) {
    if (!bridge) return fetch(url, options);
    process.stdout.write('@HTTP:' + JSON.stringify({ url, method: options.method,
        headers: options.headers, body: options.body }) + '\n');
    const reply = await replies.next();
    if (reply.done) throw Error('Puente MockMvc cerrado');
    const data = JSON.parse(reply.value);
    return { status: data.status, text: async () => data.body };
}
const replace = value => value.replace(/\{\{([^}]+)\}\}/g, (_, key) => {
    if (!environment.has(key)) throw Error(`Variable ausente: ${key}`);
    return String(environment.get(key));
});
const checks = [];
const results = [];
let current;
const pm = {
    environment: { get: key => environment.get(key), set: (key, value) => environment.set(key, value), unset: key => environment.delete(key) },
    test(name, callback) { callback(); checks.push({ request: current.name, name, passed: true }); },
    response: null
};
function scripts(item, phase) {
    for (const event of item.event || []) {
        if (event.listen === phase) vm.runInNewContext(event.script.exec.join('\n'), { pm }, { timeout: 3000 });
    }
}
try {
    for (const item of collection.item) {
        current = item;
        scripts(item, 'prerequest');
        const request = item.request;
        const url = replace(request.url);
        const headers = Object.fromEntries(request.header.map(h => [h.key, replace(h.value)]));
        const response = await transport(url, { method: request.method, headers,
            body: request.body ? replace(request.body.raw) : undefined, redirect: 'manual', signal: AbortSignal.timeout(30000) });
        const text = await response.text();
        pm.response = { json: () => JSON.parse(text), code: response.status,
            to: { have: { status: expected => assert.equal(response.status, expected, item.name) } } };
        scripts(item, 'test');
        results.push({ request: item.name, status: response.status, passed: true });
        console.log(`${String(results.length).padStart(3)}  ${response.status}  ${item.name}`);
    }
    console.log(`OK: ${results.length} solicitudes, ${checks.length} verificaciones.`);
} catch (error) {
    results.push({ request: current?.name, passed: false, error: error.message });
    console.error(`FALLO: ${current?.name}: ${error.message}`);
    process.exitCode = 1;
} finally {
    fs.mkdirSync(path.join(root, 'target'), { recursive: true });
    fs.writeFileSync(path.join(root, 'target/postman-report.json'), JSON.stringify({ results, checks }, null, 2));
    bridge?.close();
}
