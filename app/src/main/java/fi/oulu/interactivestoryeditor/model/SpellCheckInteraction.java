package fi.oulu.interactivestoryeditor.model;

import java.io.Serializable;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class SpellCheckInteraction extends Interaction implements Serializable{

    private String word;

    public SpellCheckInteraction() {
        super();
    }

    public SpellCheckInteraction(int interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl, String word) {
        super(interactionType, instructions, positiveTextFeedback, negativeTextFeedback, positiveAudioFeedbackUrl, negativeAudioFeedbackUrl);
        this.word = word;
    }

    public SpellCheckInteraction(int interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl, String word, long interaction_id) {
        super(interactionType, instructions, positiveTextFeedback, negativeTextFeedback, positiveAudioFeedbackUrl, negativeAudioFeedbackUrl, interaction_id);
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
