import hashlib

input = 'ckczppom'
i = 0
while True:
				m = hashlib.md5()
				m.update(input + str(i))

				if m.hexdigest().startswith("000000"):
								print str(i) + ": " + m.hexdigest()
								break
				
				i += 1