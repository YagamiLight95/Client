package com.client;

import java.time.Duration;

import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class MainClient {

	//static JedisPool jp;
	
	public static void main(String[] args) {
		
		System.out.println("Script partito...");
		try {
		//	jp = new JedisPool(buildPoolConfig(),"172.30.118.49", 6379);
			Jedis jClient = new Jedis("172.30.118.49", 6379); 
					//jp.getResource();
					
			jClient.auth(System.getenv("REDIS_PWD"));
			jClient.connect();
			jClient.subscribe(new JedisPubSub() {
			    @Override
			    public void onMessage(String channel, String message) {
			    	String user ="";
			    	do{
			    		user = jClient.rpop("Users");
			    		System.out.println(user);
			    		user = jClient.rpop("Users");
			    	}while(user!=null);
			    }
			}, "Nuovi_Utenti");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	private static JedisPoolConfig buildPoolConfig() {
	    final JedisPoolConfig poolConfig = new JedisPoolConfig();
	    poolConfig.setMaxTotal(128);
	    poolConfig.setMaxIdle(128);
	    poolConfig.setMinIdle(16);
	    poolConfig.setTestOnBorrow(true);
	    poolConfig.setTestOnReturn(true);
	    poolConfig.setTestWhileIdle(true);
	    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
	    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
	    poolConfig.setNumTestsPerEvictionRun(3);
	    poolConfig.setBlockWhenExhausted(true);
	    return poolConfig;
	}
	
	*/
}
