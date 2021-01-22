package com.monresto.acidlabs.monresto.Model;

public class FAQ {
    private String question;
    private String answer;

    public FAQ(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return "Question: " + question + " - Answer: " + answer;
    }
}
