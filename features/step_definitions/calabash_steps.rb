require 'calabash-android/calabash_steps'

Then /^I see object "([^\"]*)"$/ do |text|
  not query(text).empty?
end