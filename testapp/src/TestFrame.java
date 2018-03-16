
import de.elbe5.base.data.XmlData;
import de.elbe5.base.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class TestFrame extends JFrame{

    JPanel contentPane;

    JTextField input =new JTextField("c:/Source/Bandika/resources/templates/templates.xml");
    JButton button =new JButton();
    JTextArea output = new JTextArea();
    JLabel resultLabel=new JLabel();

    public TestFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception  {
        this.setSize(new Dimension(800, 400));
        this.setTitle("Test");
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        contentPane = (JPanel) this.getContentPane();
        BorderLayout layout=new BorderLayout(10,10);
        contentPane.setLayout(layout);
        JPanel inputPane = new JPanel();
        inputPane.setLayout(new BorderLayout(10,10));
        inputPane.add(new JLabel(" Input:"),BorderLayout.LINE_START);
        inputPane.add(input,BorderLayout.CENTER);
        button.setText("Run");
        button.addActionListener(e -> button_actionPerformed());
        inputPane.add(button,BorderLayout.LINE_END);
        contentPane.add(inputPane,BorderLayout.PAGE_START);
        output.setFont(font);
        contentPane.add(new JScrollPane(output),BorderLayout.CENTER);
        resultLabel.setText(" ");
        contentPane.add(resultLabel,BorderLayout.PAGE_END);
    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

    public void button_actionPerformed() {
        String path=input.getText();
        try{
            String code= FileUtil.readTextFile(path);
            output.setText(code);
        }
        catch (Exception e){
            output.setText(e.getMessage());
        }
    }

}
