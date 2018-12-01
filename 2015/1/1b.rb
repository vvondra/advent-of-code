
floor = 0
position = 1
File.read("input").split("").each do |i|
	if i == "("
		floor += 1
	elsif i == ")"
		floor -= 1
	end

  if floor == -1
		puts position
		break
	end

  position += 1
end

puts floor