#!/usr/bin/env python3
"""Authenticated calls to this project's disposable Poppeteer client. Never prints discovery tokens."""
import argparse
import json
from pathlib import Path
import socket

parser = argparse.ArgumentParser()
parser.add_argument('method')
parser.add_argument('params', nargs='?', default='{}')
parser.add_argument('--game-dir', default='run/client')
parser.add_argument('--output')
args = parser.parse_args()
root = Path(args.game_dir).resolve()
discoveries = sorted((root / 'poppeteer/instances').glob('*.json'), key=lambda p: p.stat().st_mtime, reverse=True)
if not discoveries:
    raise SystemExit('No Poppeteer instance in the specified game directory')
discovery = json.loads(discoveries[0].read_text())
if discovery['host'] not in ('127.0.0.1', '::1', 'localhost'):
    raise SystemExit('Audit client must be on loopback')
with socket.create_connection((discovery['host'], discovery['port']), timeout=60) as connection:
    stream = connection.makefile('rwb')
    def rpc(ident, method, params):
        stream.write((json.dumps({'jsonrpc': '2.0', 'id': ident, 'method': method, 'params': params}) + '\n').encode())
        stream.flush()
        response = json.loads(stream.readline())
        if 'error' in response:
            raise RuntimeError(response['error'])
        return response['result']
    rpc(1, 'poppeteer.authenticate', {'token': discovery['token']})
    result = rpc(2, args.method, json.loads(args.params))
    output = json.dumps(result, indent=2)
    if args.output:
        Path(args.output).write_text(output+'\n')
        print('Saved result to', args.output)
    else:
        print(output)
