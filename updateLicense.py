import sys
import os
import re
import shutil

FILES = []
LICENSE = []
TEMP_SUFFIX = '.tmp'

def scanfiles(basedir):
	print basedir
	global FILES
	pattern = re.compile(r'.*\.(java)')
	patternOfCompileDir = re.compile(r'.*\/((bin|gen)(\/.*)?|\..*)$')
	for dirpath, dirnames, filenames in os.walk(basedir):
		if patternOfCompileDir.match(dirpath):
			continue
		for filename in filenames:
			type = filename[filename.rfind('.'):]
			if len(type) < 1:
				continue
			match = pattern.match(type)
			if match:
				FILES.append(os.path.join(dirpath, filename))

	print r'SCAN FILE RESULT LIST'
	print r'---------------------'
	for filename in FILES:
		print filename
	print 'count is {0}'.format(len(FILES))
	print r'---------------------'

def readLicense():
	global LICENSE
	with open ('LICENSE.txt', 'r') as file:
		LICENSE = file.readlines()
	print 'LICENSE'
	print r'---------------------'
	for line in LICENSE:
		print line[:line.rfind('\n')]
	print r'---------------------'

def updateLicense():
	global FILES
	global LICENSE
	patternOfStart = re.compile(r'^\s*\/\s*\*.*')
	patternOfEnd = re.compile(r'.*\*\s*\/\s*$')
	startMatch = ''
	endMatch = ''
	print 'start update...'
	for filename in FILES:
		with open (filename, 'rw') as file:
			lines = file.readlines()
			if len(lines) < 1: 
				continue
			startMatch = patternOfStart.match(lines[0])
			if startMatch:
				##print 'match header'
				for index, line in enumerate(lines):
					endMatch = patternOfEnd.match(line)
					if endMatch:
						print 'old license is from {0} to {1}'.format(0, index)
						writeToTmpFile(filename, LICENSE, lines[index + 1:])
						replaceTmpFile(filename)
						print 'modify ' + filename
						break
			else:
				##print 'add license'
				writeToTmpFile(filename, LICENSE, lines)
				replaceTmpFile(filename)
				print 'modify ' + filename
	print 'end update'

def createTmpFile(filename):
	return filename + TEMP_SUFFIX;

def writeToTmpFile(filename, *lines):
	if len(lines) < 1:
		return
	with open (createTmpFile(filename), 'w') as file:
		for alines in lines:
			if len(alines) < 1:
				continue
			for line in alines:
				##print >> file, line
				file.write('%s' % line)

def replaceTmpFile(filename):
	shutil.move(createTmpFile(filename), filename)

if __name__ == "__main__":
	if len(sys.argv) < 2:
		sys.exit('please enter a root dir which will be iterated to update license.')
	scanfiles(sys.argv[1])
	readLicense()	
	updateLicense()	
