import json
import os
import re
import shutil
import subprocess
import sys

MIN_PYTHON = (3, 8)
if sys.version_info < MIN_PYTHON:
    sys.exit("Python %s.%s or later is required.\n" % MIN_PYTHON)

autoyes = False
config = 'deploy-local.json'
for arg in sys.argv[1:]:
    if arg == '-y' or arg == '--y' or arg == '-yes' or arg == '--yes':
        autoyes = True
    # since python 3.8
    elif match := re.match(r'^-?-(c|config)=(?P<config>.+\.json)$', arg):
        config = match.group('config')
    elif arg == '-h':
        print('Use python3 deploy-local.py')
        print('\t-y\tAutomatically accepts endpoint file remove operations')
        exit(0)
    elif arg.startswith('-'):
        print('Unknown argument: %s' % arg)
        exit(0)

with open(config, 'r') as file:
    jsonConfig = json.load(file)
    endpoint = jsonConfig['endpoint']
    endpoint = endpoint.replace('~', os.path.expanduser('~'))
    buildRegex = re.compile(jsonConfig['build-regex'])
    buildCommand = jsonConfig['build-command']

builds = [f for f in os.listdir('target/') if re.search(buildRegex, f)]

def getLatestBuild():
    latest = {'nBuild': 0}
    for file in os.listdir('target/'):
        match = re.search(buildRegex, file)
        if match:
            nBuild = match.group('nBuild')
            if latest['nBuild'] < int(nBuild):
                latest['file'] = file
                latest['nBuild'] = int(nBuild)
    return latest

latestBuild = getLatestBuild()
while not 'file' in latestBuild:
    print('No build found.')
    answer = input('Try and rebuild? (y/n) ').lower()
    if answer.startswith('y'):
        subprocess.run(buildCommand.split())
        latestBuild = getLatestBuild()
    elif answer.startswith('n'):
        exit(0)

print('The latest build: %s' % latestBuild['file'])

for file in os.listdir(endpoint):
    match = re.search(buildRegex, file)
    if match:
        if autoyes:
            print('Removed %s file from the endpoint.' % file)
            os.remove(os.path.join(endpoint, file))
            continue
        while True:
            answer = input('Remove %s file from the endpoint? (y/n) ' % file).lower()
            if answer.startswith('y'):
                os.remove(os.path.join(endpoint, file))
                break
            elif answer.startswith('n'):
                break

shutil.copy2(os.path.join(os.getcwd(), 'target', latestBuild['file']), endpoint)
print('cp %s -> %s' % (latestBuild['file'], endpoint))
