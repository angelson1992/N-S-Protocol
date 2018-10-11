# N-S-Protocol

There are a couple assumptions that were made either for the sake of time or convenience. The names and ids of the clients are assumed
to be unique. In addition, they are assumed to not colide when hashed. The number alpha is assumed to be a primitive root of the large
prime number that is acting as the modulus. It is assumed that the user will pick suitible values for encouraging a secure system, e.g.
large enough prime numbers and such.

I protected my N-S Protocol from replay attacks by having my nonce be a direct function of time and checking it's value against the clock
occassionally. This of course means there is an assumption of all systemed having synchronized clocks. 
