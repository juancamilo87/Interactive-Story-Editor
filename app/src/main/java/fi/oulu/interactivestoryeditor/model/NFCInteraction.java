package fi.oulu.interactivestoryeditor.model;

import java.io.Serializable;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class NFCInteraction extends Interaction implements Serializable{

    private String secretCode;

    public NFCInteraction() {
        super();
    }

    public NFCInteraction(int interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl, String secretCode) {
        super(interactionType, instructions, positiveTextFeedback, negativeTextFeedback, positiveAudioFeedbackUrl, negativeAudioFeedbackUrl);
        this.secretCode = secretCode;
    }

    public NFCInteraction(int interactionType, String instructions, String positiveTextFeedback, String negativeTextFeedback, String positiveAudioFeedbackUrl, String negativeAudioFeedbackUrl, String secretCode, long interaction_id) {
        super(interactionType, instructions, positiveTextFeedback, negativeTextFeedback, positiveAudioFeedbackUrl, negativeAudioFeedbackUrl, interaction_id);
        this.secretCode = secretCode;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
