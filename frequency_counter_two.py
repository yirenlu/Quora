#
# This is a python script to produce the csv input files for weka.
# Execution is "python 
#

#!/usr/bin/python
import csv
import nltk
import sys
import string
from string import punctuation
from nltk.book import *
from nltk import bigrams
from collections import Counter

# getting command line arguments

# normalize yes/no?
string = sys.argv[1]

# how many unigrams desired?
oneword = int(sys.argv[2])

# how many bigrams desired?
twoword = int(sys.argv[3])

# data file
data_file = sys.argv[4]

# open data file
f = open(data_file)
text = f.read()

# generating set of unigrams
words = text.lower().split()
fdist = FreqDist(words)
vocab = fdist.keys()

# generating set of bigrams
two = bigrams(words)
vocab2 = FreqDist(two).keys()

# generating set of trigrams
#three = trigrams(words)
#vocab3 = FreqDist(three).keys()

# opening the output file
if (sys.argv[1] == 'normalize'):
	write = csv.writer(open('normalize_features_ten_topics'+ sys.argv[2] + '_unigrams_' + sys.argv[3] + '_bigrams.csv', 'wb'))
else:
	write = csv.writer(open('features_ten_topics_'+ sys.argv[2] + '_unigrams_' + sys.argv[3] + '_bigrams.csv', 'wb'))


# printing header row for the csv file; am using just a sequence of numbers due 
# to the complexities of using actual words (have to escape EVERYTHING!)	
header = list()
for x in range(0, oneword + twoword):
	header.append(x)
header.append('perspicacious')
write.writerow(header)

print "printing..."

# generating an input file will the topics below in it
counter('Startups', string, oneword, twoword)
counter('Movies', string, oneword, twoword)
counter('Food', string, oneword, twoword)
counter('Parenting', string, oneword, twoword)
counter('Politics', string, oneword, twoword)
counter('Travel', string, oneword, twoword)
counter('Science', string, oneword, twoword)
counter('History', string, oneword, twoword)
counter('Facebook-1', string, oneword, twoword)
counter('Books', string, oneword, twoword)
counter('Music', string, oneword, twoword)
counter('Twitter-1', string, oneword, twoword)

# function to get and print most frequent words
def counter(topic, string, oneword, twoword):

	print topic
	g = open('crawler_output_'+topic+'.txt')
	startups_readlines = g.readlines()
	n = len(startups_readlines)

	for x in range(1, n):
		cnt = list()
		fdist_startups = FreqDist(startups_readlines[x].lower().split())
		number_words = len(startups_readlines[x].lower().split())
		for word in vocab[:oneword]:
			if word in fdist_startups:
				if (string == 'normalize'):
					cnt.append(fdist_startups[word]/number_words)
				else:
					cnt.append(fdist_startups[word])
			else:
				cnt.append(0)	
		fdist_startups_bigrams = FreqDist(bigrams(startups_readlines[x].lower().split()))
		for word in vocab2[:twoword]:
			if word in fdist_startups_bigrams:
				if (string == 'normalize'):
					cnt.append(fdist_startups_bigrams[word]/(number_words -1))
				else:
					cnt.append(fdist_startups_bigrams[word])
			else:
				cnt.append(0)	
		cnt.append(topic)
		write.writerow(cnt)
