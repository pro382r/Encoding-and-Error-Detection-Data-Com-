import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class NetworkEncodingErrorDetectionSwingCustomized extends JFrame {
    private JTextField inputTextField;
    private JTextArea outputTextArea;
    private JComboBox<String> encodingComboBox;
    private JComboBox<String> errorDetectionComboBox;
    private JCheckBox encodingCheckBox;
    private JCheckBox errorDetectionCheckBox;
    private JButton processButton;

    public NetworkEncodingErrorDetectionSwingCustomized() {
        setTitle("Colorful Network Encoding & Error Detection");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // Alice Blue

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        inputPanel.setBackground(new Color(190, 255, 255));
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel inputLabel = new JLabel("Input Bits: ");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(inputLabel);

        inputTextField = new JTextField(20);
        inputTextField.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputPanel.add(inputTextField);

        JLabel encodingLabel = new JLabel("Encoding:");
        encodingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(encodingLabel);

        encodingComboBox = new JComboBox<>(new String[]{"NRZ-L", "NRZ-I", "Manchester"});
        encodingComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        encodingComboBox.setBackground(Color.WHITE);
        inputPanel.add(encodingComboBox);

        JLabel errorDetectionLabel = new JLabel("Error Detection:");
        errorDetectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(errorDetectionLabel);

        errorDetectionComboBox = new JComboBox<>(new String[]{"Parity Check", "Hamming Code", "CRC"});
        errorDetectionComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        errorDetectionComboBox.setBackground(Color.WHITE);
        inputPanel.add(errorDetectionComboBox);

        encodingCheckBox = new JCheckBox("Perform Encoding");
        encodingCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        encodingCheckBox.setBackground(new Color(192, 255, 255));
        inputPanel.add(encodingCheckBox);

        errorDetectionCheckBox = new JCheckBox("Perform Error Detection");
        errorDetectionCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        errorDetectionCheckBox.setBackground(new Color(190, 255, 255));
        inputPanel.add(errorDetectionCheckBox);

        processButton = new JButton("Process");
        processButton.setFont(new Font("Arial", Font.BOLD, 16));
        processButton.setBackground(new Color(0, 128, 128)); // Teal
        processButton.setForeground(Color.WHITE);
        processButton.setFocusPainted(false);
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processData();
            }
        });
        inputPanel.add(processButton);

        add(inputPanel, BorderLayout.NORTH);

        outputTextArea = new JTextArea();
        outputTextArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setBorder(new LineBorder(new Color(140, 200, 220), 2)); // Light Blue border
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void processData() {
        String inputBits = inputTextField.getText();
        boolean performEncoding = encodingCheckBox.isSelected();
        boolean performErrorDetection = errorDetectionCheckBox.isSelected();

        if (!performEncoding && !performErrorDetection) {
            outputTextArea.setForeground(Color.RED);
            outputTextArea.append("Please select at least one option (Encoding or Error Detection).\n");
            outputTextArea.setForeground(Color.BLACK); // Reset text color
            return;
        }

        List<Integer> bits = new ArrayList<>();
        for (char c : inputBits.toCharArray()) {
            if (c == '0' || c == '1') {
                bits.add(Character.getNumericValue(c));
            } else {
                outputTextArea.setForeground(Color.RED);
                outputTextArea.append("Invalid input. Please enter only 0s and 1s.\n");
                outputTextArea.setForeground(Color.BLACK);
                return;
            }
        }

        outputTextArea.append("-------------------------------------------------------\n");

        if (performEncoding) {
            String encodingMethod = (String) encodingComboBox.getSelectedItem();
            List<Integer> encodedSignal = encode(bits, encodingMethod);
            List<Integer> noisySignal = simulateNoisyChannel(encodedSignal);
            List<Integer> decodedBits = decode(noisySignal, encodingMethod);

            outputTextArea.append("Encoding Method: " + encodingMethod + "\n");
            outputTextArea.append("Original Bits: " + bits + "\n");
            outputTextArea.append("Encoded Signal: " + encodedSignal + "\n");
            outputTextArea.append("Noisy Signal: " + noisySignal + "\n");
            outputTextArea.append("Decoded Bits: " + decodedBits + "\n\n");
        }

        if (performErrorDetection) {
            String errorDetectionMethod = (String) errorDetectionComboBox.getSelectedItem();
            outputTextArea.append("Error Detection Method: " + errorDetectionMethod + "\n");
            List<Integer> bitsForErrorDetection = performEncoding ? decode(simulateNoisyChannel(encode(bits, (String) encodingComboBox.getSelectedItem())), (String) encodingComboBox.getSelectedItem()) : bits;
            errorDetection(bitsForErrorDetection, errorDetectionMethod);
        }

        outputTextArea.append("-------------------------------------------------------\n");
    }

    private List<Integer> encode(List<Integer> bits, String encodingMethod) {
        List<Integer> encodedSignal = new ArrayList<>();
        if (encodingMethod.equalsIgnoreCase("NRZ-L")) {
            encodedSignal.addAll(bits);
        } else if (encodingMethod.equalsIgnoreCase("NRZ-I")) {
            int currentLevel = 1;
            for (int bit : bits) {
                if (bit == 1) {
                    currentLevel = 1 - currentLevel;
                }
                encodedSignal.add(currentLevel);
            }
        } else if (encodingMethod.equalsIgnoreCase("Manchester")) {
            for (int bit : bits) {
                if (bit == 0) {
                    encodedSignal.add(1);
                    encodedSignal.add(0);
                } else {
                    encodedSignal.add(0);
                    encodedSignal.add(1);
                }
            }
        }
        return encodedSignal;
    }

    private List<Integer> simulateNoisyChannel(List<Integer> signal) {
        List<Integer> noisySignal = new ArrayList<>(signal);
        Random random = new Random();
        for (int i = 0; i < noisySignal.size(); i++) {
            if (random.nextDouble() < 0.05) { // 5% chance of error
                noisySignal.set(i, 1 - noisySignal.get(i));
            }
        }
        return noisySignal;
    }

    private List<Integer> decode(List<Integer> signal, String encodingMethod) {
        List<Integer> decodedBits = new ArrayList<>();
        if (encodingMethod.equalsIgnoreCase("NRZ-L")) {
            decodedBits.addAll(signal);
        } else if (encodingMethod.equalsIgnoreCase("NRZ-I")) {
            int previousLevel = 1;
            for (int level : signal) {
                if (level != previousLevel) {
                    decodedBits.add(1);
                } else {
                    decodedBits.add(0);
                }
                previousLevel = level;
            }
        } else if (encodingMethod.equalsIgnoreCase("Manchester")) {
            for (int i = 0; i < signal.size(); i += 2) {
                if (signal.get(i) == 0 && signal.get(i + 1) == 1) {
                    decodedBits.add(1);
                } else {
                    decodedBits.add(0);
                }
            }
        }
        return decodedBits;
    }

    private void errorDetection(List<Integer> bits, String errorDetectionMethod) {
        if (errorDetectionMethod.equalsIgnoreCase("Parity Check")) {
            int count = 0;
            for (int bit : bits) {
                if (bit == 1) {
                    count++;
                }
            }
            if (count % 2 == 0) {
                outputTextArea.setForeground(new Color(0, 100, 0)); // Dark Green
                outputTextArea.append("Parity Check: No Error Detected\n");
                outputTextArea.setForeground(Color.BLACK);
            } else {
                outputTextArea.setForeground(Color.RED);
                outputTextArea.append("Parity Check: Error Detected\n");
                outputTextArea.setForeground(Color.BLACK);
            }
        } else if (errorDetectionMethod.equalsIgnoreCase("Hamming Code")) {
            hammingCode(bits);
        } else if (errorDetectionMethod.equalsIgnoreCase("CRC")) {
            crc(bits);
        }
    }

    private void hammingCode(List<Integer> bits) {
        int m = bits.size();
        int r = 1;
        while (Math.pow(2, r) < m + r + 1) {
            r++;
        }
        List<Integer> hammingCode = new ArrayList<>();
        int dataIndex = 0;
        for (int i = 1; i <= m + r; i++) {
            if (Math.pow(2, (int) (Math.log(i) / Math.log(2))) == i) {
                hammingCode.add(0); // Parity bit
            } else {
                hammingCode.add(bits.get(dataIndex++)); // Data bit
            }
        }
        for (int i = 0; i < r; i++) {
            int parityBitIndex = (int) Math.pow(2, i) - 1;
            int parity = 0;
            for (int j = 0; j < hammingCode.size(); j++) {
                if (((j + 1) >> i & 1) == 1) {
                    parity ^= hammingCode.get(j);
                }
            }
            hammingCode.set(parityBitIndex, parity);
        }
        outputTextArea.append("Hamming Encoded: " + hammingCode + "\n");
        Random rand = new Random();
        List<Integer> receivedCode = new ArrayList<>(hammingCode);
        boolean errorIntroduced = false;
        if (rand.nextDouble() < 0.1) {
            int errorIndex = rand.nextInt(receivedCode.size());
            receivedCode.set(errorIndex, 1 - receivedCode.get(errorIndex));
            outputTextArea.setForeground(new Color(255, 140, 0)); // Dark Orange
            outputTextArea.append("Hamming Encoded with Error at index " + (errorIndex + 1) + ": " + receivedCode + "\n");
            outputTextArea.setForeground(Color.BLACK);
            errorIntroduced = true;
        }

        int errorIndex = 0;
        for (int i = 0; i < r; i++) {
            int parityBitIndex = (int) Math.pow(2, i) - 1;
            int parity = 0;
            for (int j = 0; j < receivedCode.size(); j++) {
                if (((j + 1) >> i & 1) == 1) {
                    parity ^= receivedCode.get(j);
                }
            }
            if (parity == 1) {
                errorIndex += (int) Math.pow(2, i);
            }
        }

        if (errorIndex != 0) {
            outputTextArea.setForeground(Color.RED);
            outputTextArea.append("Hamming Code: Error Detected at index " + errorIndex + "\n");
            if (errorIndex <= receivedCode.size()) {
                receivedCode.set(errorIndex - 1, 1 - receivedCode.get(errorIndex - 1));
                outputTextArea.setForeground(new Color(0, 100, 0)); // Dark Green
                outputTextArea.append("Hamming Corrected Code: " + receivedCode + "\n");
            }
            outputTextArea.setForeground(Color.BLACK);
        } else {
            outputTextArea.setForeground(new Color(0, 100, 0)); // Dark Green
            outputTextArea.append("Hamming Code: No Error Detected\n");
            outputTextArea.setForeground(Color.BLACK);
        }
        outputTextArea.append("\n");
    }

    private void crc(List<Integer> bits) {
        int[] polynomial = {1, 0, 0, 0, 0, 0, 1, 1, 1}; // Example CRC-8 polynomial
        int polynomialLength = polynomial.length;
        List<Integer> paddedBits = new ArrayList<>(bits);
        for (int i = 0; i < polynomialLength - 1; i++) {
            paddedBits.add(0);
        }
        List<Integer> remainder = new ArrayList<>(paddedBits);
        for (int i = 0; i <= bits.size() - 1; i++) {
            if (remainder.get(i) == 1) {
                for (int j = 0; j < polynomialLength; j++) {
                    remainder.set(i + j, remainder.get(i + j) ^ polynomial[j]);
                }
            }
        }
        List<Integer> crc = remainder.subList(bits.size(), paddedBits.size());
        List<Integer> transmittedBits = new ArrayList<>(bits);
        transmittedBits.addAll(crc);
        outputTextArea.append("CRC Encoded: " + transmittedBits + "\n");
        Random rand = new Random();
        List<Integer> receivedBits = new ArrayList<>(transmittedBits);
        boolean errorIntroduced = false;
        if (rand.nextDouble() < 0.1) {
            int errorIndex = rand.nextInt(receivedBits.size());
            receivedBits.set(errorIndex, 1 - receivedBits.get(errorIndex));
            outputTextArea.setForeground(new Color(255, 140, 0)); // Dark Orange
            outputTextArea.append("CRC Encoded with Error at index " + (errorIndex + 1) + ": " + receivedBits + "\n");
            outputTextArea.setForeground(Color.BLACK);
            errorIntroduced = true;
        }

        remainder = new ArrayList<>(receivedBits);
        for (int i = 0; i <= bits.size() - 1; i++) {
            if (remainder.get(i) == 1) {
                for (int j = 0; j < polynomialLength; j++) {
                    remainder.set(i + j, remainder.get(i + j) ^ polynomial[j]);
                }
            }
        }
        boolean error = false;
        for (int i = bits.size(); i < remainder.size(); i++) {
            if (remainder.get(i) == 1) {
                error = true;
                break;
            }
        }
        if (error) {
            outputTextArea.setForeground(Color.RED);
            outputTextArea.append("CRC: Error Detected\n\n");
            outputTextArea.setForeground(Color.BLACK);
        } else {
            outputTextArea.setForeground(new Color(0, 100, 0)); // Dark Green
            outputTextArea.append("CRC: No Error Detected\n\n");
            outputTextArea.setForeground(Color.BLACK);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetworkEncodingErrorDetectionSwingCustomized::new);
    }
}
