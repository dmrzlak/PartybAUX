package com.example.partybauxserver.client;

import java.util.ArrayList;

public class Greeting {

    private String content;
    private ArrayList<String> list;

    public Greeting() {
    }

    public Greeting(String content) {
        this.content = content;
    }

    public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
        return content;
    }

}
