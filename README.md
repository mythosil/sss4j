sss4j (Secret Sharing Scheme for Java)
======================================

[![Build Status](https://travis-ci.org/mythosil/sss4j.svg?branch=master)](https://travis-ci.org/mythosil/sss4j)

Library project of Shamir's Secret Sharing Scheme.

## Example

```java
byte[] secret = "wanna make this secret".getBytes();

// (2, 3)-threshold secret share
List<Share> shares = Sss4j.split(secret, 2, 3);

// reconstruct secret from 2 shares only
List<Share> twoShares = shares.subList(0, 2);
byte[] combined = Sss4j.combine(twoShares);

// secret == combined

// issue another (4th) share
Share anotherShare = Sss4j.issue(shares, 4);
```

## License
- [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
