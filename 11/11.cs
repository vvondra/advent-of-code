using System;
using System.Collections.Generic;
using System.Linq;

namespace Application
{
	class Day11
	{
		public static void Main(string[] args)
		{
			System.Console.WriteLine (String.Join(
				",",
				new PasswordGenerator ("hxbxwxba").Take (2).ToArray()
			));
		}
	}

	class PasswordGenerator : IEnumerable<String> {
		String basePassword;

		public PasswordGenerator(String basePassword) {
			this.basePassword = basePassword;
		}

		public IEnumerator<String> GetEnumerator ()
		{
			String password = basePassword;
			while (true) {
				password = NextString (password);

				if (!IsValid(password)) {
					continue;
				}

				yield return password;
			}
		}

		private static bool IsValid(String password) {
			System.Console.Write ("Next candidate " + password + " ");

			var valid = ContainsNoAmbiguousChars (password) &&
				ContainsRisingSequence (password) &&
				ContainsTwoPairs (password);

			System.Console.WriteLine ();

			return valid;
		}

		private static bool ContainsNoAmbiguousChars (string password)
		{
			var valid = !password.Contains ("i") &&
				!password.Contains ("o") &&
				!password.Contains ("l");
			System.Console.Write (valid ? "Y" : "N");

			return valid;
		}

		private static bool ContainsRisingSequence (string password)
		{
			var chars = password.ToCharArray();
			var sequenceLength = 1;
			char previousChar = chars[0];
			for (int i = 1; i < chars.Length; i++) {
				if (chars [i] == previousChar + 1) {
					sequenceLength++;

					if (sequenceLength == 3) {
						System.Console.Write ("Y");

						return true;
					}
				} else {
					sequenceLength = 1;
				}

				previousChar = chars [i];
			}

			System.Console.Write ("N");
			return false;
		}

		private static bool ContainsTwoPairs (string password)
		{
			var chars = password.ToCharArray();
			char previousChar = chars[0];
			var pairCount = 0;
			for (int i = 1; i < chars.Length; i++) {
				if (chars [i] == previousChar) {
					pairCount++;
					i++;

					if (i == chars.Length) {
						break;
					}
				}
				previousChar = chars [i];
			}
			System.Console.Write (pairCount > 1 ? "Y" : "N");

			return pairCount > 1;
		}

		private static String NextString(String password) {
			int length = password.Length;

			char c = password[length - 1];

			if (c == 'z') {
				return NextString (password.Substring (0, length - 1)) + 'a';
			}

			return String.Format("{0}{1}", password.Substring (0, length - 1), (char)((int) c + 1));
		}

		System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator ()
		{
			return GetEnumerator ();
		}

	}
}
