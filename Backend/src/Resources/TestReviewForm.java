package Resources;

import javax.swing.*;
import java.awt.*;

public class TestReviewForm {
    private static int reviewIdCounter = 3;

    public void newReviewForm() {
        JFrame frame = new JFrame("Write a Review");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField toiletIdField = new JTextField();
        JTextField toiletNameField = new JTextField();
        JTextField ratingField = new JTextField();
        JTextArea descriptionArea = new JTextArea();
        JTextField photoField = new JTextField();

        frame.add(new JLabel("Id:"));
        frame.add(idField);

        frame.add(new JLabel("Author:"));
        frame.add(authorField);

        frame.add(new JLabel("Date:"));
        frame.add(dateField);

        frame.add(new JLabel("Toilet id:"));
        frame.add(toiletIdField);

        frame.add(new JLabel("Toilet name:"));
        frame.add(toiletNameField);

        frame.add(new JLabel("Rating (1–5):"));
        frame.add(ratingField);

        frame.add(new JLabel("Description:"));
        frame.add(new JScrollPane(descriptionArea));

        frame.add(new JLabel("Toilet photo:"));
        frame.add(photoField);

        JButton submitBtn = new JButton("Submit");
        frame.add(new JLabel()); // spacer
        frame.add(submitBtn);

        submitBtn.addActionListener(e -> {
            int id = reviewIdCounter++;
            String author = authorField.getText();
            String date = dateField.getText();
            int toiletId = Integer.parseInt(toiletIdField.getText());
            String toiletName = toiletIdField.getText();
            String rating = ratingField.getText();
            String description = descriptionArea.getText();
            String photo = photoField.getText();

            System.out.println(id + " | " + author + " | " + date + " | " + toiletId + " | " + toiletName + " | " + rating + " | " + description + " | " + photo);
            frame.dispose();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
