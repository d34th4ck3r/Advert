MATCH (z1:Zipcode{value:"6900888"})<-[:TravelledTo]-(hmac)-[:TravelledTo]->(z2:Zipcode{value:"6900889"}) return hmac;
