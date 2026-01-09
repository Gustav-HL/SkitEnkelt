package Resources;

import javax.swing.*;
import java.awt.*;

public class TestReviewForm {

    public void newReviewForm() {
        JFrame frame = new JFrame("Write a Review");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2, 5, 5));

        JTextField authorField = new JTextField();
        JTextField toiletField = new JTextField();
        JTextField ratingField = new JTextField();
        JTextArea descriptionArea = new JTextArea();

        frame.add(new JLabel("Author:"));
        frame.add(authorField);

        frame.add(new JLabel("Toilet name:"));
        frame.add(toiletField);

        frame.add(new JLabel("Rating (1–5):"));
        frame.add(ratingField);

        frame.add(new JLabel("Description:"));
        frame.add(new JScrollPane(descriptionArea));

        JButton submitBtn = new JButton("Submit");
        frame.add(new JLabel()); // spacer
        frame.add(submitBtn);

        submitBtn.addActionListener(e -> {
            String author = authorField.getText();
            String toilet = toiletField.getText();
            String rating = ratingField.getText();
            String description = descriptionArea.getText();

            System.out.println(author + " | " + toilet + " | " + rating + " | " + description);
            frame.dispose();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
