import javax.swing.*;
import java.util.*;

public class QuizMode {
    public DrawPanel drawPanel;

    public QuizMode(DrawPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    public void start() {
        Molecule molecule = drawPanel.molecule;

        if (molecule == null || molecule.atoms.size() == 0) {
            JOptionPane.showMessageDialog(
                drawPanel,
                "Generate a molecule first, then start quiz mode.",
                "Quiz Mode",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Atom central = molecule.getMainAtom();

        if (central == null) {
            JOptionPane.showMessageDialog(
                drawPanel,
                "This molecule does not have enough structure data for a quiz.",
                "Quiz Mode",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ArrayList<QuizQuestion> questions = buildQuestions(molecule, central);
        int correct = 0;
        StringBuilder review = new StringBuilder();

        for (QuizQuestion question : questions) {
            String answer = JOptionPane.showInputDialog(
                drawPanel,
                question.prompt,
                "Quiz Mode",
                JOptionPane.QUESTION_MESSAGE
            );

            if (answer == null) {
                return;
            }

            if (question.isCorrect(answer)) {
                correct++;
                review.append("Correct: ").append(question.shortName).append("\n");
            }
            else {
                review
                .append("Missed: ")
                .append(question.shortName)
                .append("\nYour answer: ")
                .append(answer)
                .append("\nCorrect answer: ")
                .append(question.answer)
                .append("\n\n");
            }
        }

        JOptionPane.showMessageDialog(
            drawPanel,
            "Score: " + correct + "/" + questions.size() + "\n\n" + review.toString(),
            "Quiz Results",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public ArrayList<QuizQuestion> buildQuestions(Molecule molecule, Atom central) {
        ArrayList<QuizQuestion> questions = new ArrayList<>();

        questions.add(
            new QuizQuestion(
                "Shape",
                "What is the molecular shape?",
                formatShape(central.molecularGeometry)
            )
        );

        questions.add(
            new QuizQuestion(
                "Hybridization",
                "What is the hybridization of the central atom?",
                central.hybridization
            )
        );

        questions.add(
            new QuizQuestion(
                "Sigma Bonds",
                "How many sigma bonds are in this molecule?",
                "" + countSigmaBonds(molecule)
            )
        );

        questions.add(
            new QuizQuestion(
                "Pi Bonds",
                "How many pi bonds are in this molecule?",
                "" + countPiBonds(molecule)
            )
        );

        questions.add(
            new QuizQuestion(
                "Lone Pairs",
                "How many lone pairs are on the central atom?",
                "" + central.lonePairs
            )
        );

        Collections.shuffle(questions);

        while (questions.size() > 4) {
            questions.remove(questions.size() - 1);
        }

        return questions;
    }

    public int countSigmaBonds(Molecule molecule) {
        return molecule.bonds.size();
    }

    public int countPiBonds(Molecule molecule) {
        int pi = 0;

        for (Bond bond : molecule.bonds) {
            if (bond.type == 2) {
                pi += 1;
            }

            if (bond.type == 3) {
                pi += 2;
            }
        }

        return pi;
    }

    public String formatShape(String shape) {
        if (shape == null) {
            return "unknown";
        }

        return shape.replace("_", " ");
    }

    public static class QuizQuestion {
        String shortName;
        String prompt;
        String answer;

        QuizQuestion(String shortName, String prompt, String answer) {
            this.shortName = shortName;
            this.prompt = prompt;
            this.answer = answer;
        }

        boolean isCorrect(String userAnswer) {
            return normalize(userAnswer).equals(normalize(answer));
        }

        public String normalize(String value) {
            return value
                .trim()
                .toLowerCase()
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "");
        }
    }
}
