package com.example.projetjava.model;

public class QuestionReponse {
    public String question;
    public String solution;

    public QuestionReponse(String question,String solution){
        this.question = question;
        this.solution = solution;

}
public String getQuestion() {
        return question;
}
public void setQuestion(String question) {
        this.question = question;
}
public String getSolution() {
        return solution;
}
public void setSolution(String solution) {
        this.solution = solution;
}

}