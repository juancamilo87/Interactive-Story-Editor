package fi.oulu.interactivestoryeditor.model;

import java.io.Serializable;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class QuizInteraction extends Interaction implements Serializable{

    private String question;
    private String correctAnswer;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;

    public QuizInteraction() {
        super();
    }

    public QuizInteraction(int interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl, String question, String correctAnswer, String answer1, String answer2, String answer3, String answer4) {
        super(interactionType, instructions, positiveTextFeedback, negativeTextFeedback, positiveAudioFeedbackUrl, negativeAudioFeedbackUrl);
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }

    public QuizInteraction(int interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl, String question, String correctAnswer, String answer1, String answer2, String answer3, String answer4, long interaction_id) {
        super(interactionType, instructions, positiveTextFeedback, negativeTextFeedback, positiveAudioFeedbackUrl, negativeAudioFeedbackUrl, interaction_id);
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }
}
