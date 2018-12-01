
floor = 0

File.read("input").split("").each do |i|
	if i == "("
		floor += 1
	elsif i == ")"
		floor -= 1
	end
end

puts floor