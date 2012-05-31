require 'rubygems'
require 'nokogiri'
require 'open-uri'
require 'hpricot'
require 'watir-webdriver'
require 'csv'

require './lib/QuoraUser'
require './lib/QuoraHtmlLoader'
require './lib/About'
require './lib/Answer'
require './lib/Comment'
require './lib/Content'
require './lib/Follower'
require './lib/Following'
require './lib/Mention'
require './lib/Post'
require './lib/Question'
require './lib/QuestionTopic'
require './lib/Topic'
require './lib/Voter'
require './lib/QuestionFollowing'



def word_frequency(words, text, topic)
    	array = words.map { |word| text.scan(/\b#{word}\b/).length}
	final_array = array.push("Startups")
end


class QuoraPage
    def initialize(browser)
        @browser = browser
    end
    
    def goto(topic)
        @browser.goto 'http://www.quora.com/' + topic + '/best_questions'
        wait_questions_loaded
    end

    def goto_question(link)
	@browser.goto link
    end

    def click_more()
        @browser.div(:id, /_more/).click
        wait_questions_loaded
    end

    def get_answer(n)
	@browser.div(:class=> "feed_item_answer answer_text", :index=> n)
    end

    def questions_count()
        @browser.links(:class, 'question_link').count{ |x| x.visible? }
    end

    def answers_count()
	@browser.elements(:class, 'answer_content').size
    end

    def get_page_answer(n)
	@browser.div(:class=> "answer_content", :index => n)
    end

    def get_question_links(n)
        @browser.a(:class=>'question_link', :index=>n).href
    end

    def wait_questions_loaded()
        begin
            questions_start_count = questions_count()
            sleep(2)
        end while questions_start_count != questions_count()
    end 
end

def quora_crawler(topic, finish, boundary)
	puts topic
	page = QuoraPage.new(Watir::Browser.new :chrome)
	page.goto(topic)

	list_of_links = Array.new

	t = (finish/30)
	for n in 1...t
		puts page.questions_count
		page.click_more
	end	

	for n in 1...boundary
		puts page.get_question_links(n)
		list_of_links[n] = page.get_question_links(n)
	end

	#puts list_of_links

	File.open("crawler_output_more_data_" + topic + ".txt", "wb") do |csv|
	
	puts "before loop"

	for n in 1...boundary
		puts list_of_links[n]
		page.goto_question(list_of_links[n])
		number = page.answers_count()
		puts number
		for m in 1...number
			puts m
			if page.get_page_answer(m).exists?
				csv.puts page.get_page_answer(m).text.gsub(/\s+/, " ")
			end	
		end
	end
	

	end

	File.open("crawler_output_more_data_good_" + topic + ".txt", "w") do |ofile|
  		File.foreach("crawler_output_more_data_" + topic + ".txt") do |iline|
    		   ofile.puts(iline) unless iline.include?("B I U @ Add Answer")
		end
  	end
end

#quora_crawler('Food', 300, 302)
#quora_crawler('Movies', 180, 181)
quora_crawler('Startups', 570, 584)
quora_crawler('Parenting', 150, 169)
quora_crawler('Politics', 90, 91)
quora_crawler('Travel', 90, 96)
quora_crawler('Science',90,77)
quora_crawler('History',180,169)
quora_crawler('Facebook-1', 210, 203)
#quora_crawler('Books', 150, 141)
#quora_crawler('Music', 180, 152)
#quora_crawler('Twitter-1', 120, 97)





