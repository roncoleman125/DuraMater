/*
 Copyright (c) Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package duramater.knn.mnist.view;

import duramater.knn.mnist.controller.ILoss;
import duramater.knn.mnist.controller.L2;
import duramater.knn.mnist.model.MatrixType;
import duramater.knn.mnist.model.MnistArrays;
import duramater.knn.mnist.model.MnistMatrix;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Random;

import static duramater.knn.mnist.model.MatrixType.CANDIDATE;
import static duramater.knn.mnist.model.MatrixType.TARGET;

/**
 * Count Basic app
 * @author Ron.Coleman
 */
public class Dac extends JPanel {
    /**
     * Constructor
     */
    public Dac() {
        setLayout(new BorderLayout());

        // Uncomment this code to move the "done" button and other controls to their own panel.
//        JPanel outerPanel = new JPanel();
//        outerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        add(new OuterPanel(),BorderLayout.CENTER);
    }
}

/**
 * Contains two main panels: the side-by-side images in center and the controls at the bottom.
 */
class OuterPanel extends JPanel {
    protected TargetCandidatePanel tcp = new TargetCandidatePanel(this);
    protected NavControls cp = new NavControls(tcp,this);

    /**
     * Constructor
     */
    public OuterPanel() {
        setLayout(new BorderLayout());

        add(tcp,BorderLayout.CENTER);
        add(cp, BorderLayout.SOUTH);
    }

//    /**
//     * Updates the image for the type.
//     * @param idx Image number
//     * @param type Type TARGET or CANDIDATE
//     */
//    public void updateImage(int idx, MatrixType type) {
//        tcp.updateImage(idx,type);
//    }

    /**
     * Updates the loss report.
     */
    public void updateLoss() {
        tcp.updateLoss();
    }
    public void updateLoss(double loss) { tcp.updateLoss(loss); }
}

/**
 * One of the two main panels.
 * This one contains the target and candidate images side-by-side.
 */
class TargetCandidatePanel extends JPanel {
    // Initial default image numbers to fetch and render
    public final static int TARGET_IDX = 0;
    public final static int CANDIDATE_IDX = 1234;

    protected int targetIdx = TARGET_IDX;
    protected int candidateIdx = CANDIDATE_IDX;

    protected OuterPanel outerPanel;
    protected TitledImgPanel targetPanel;
    protected TitledImgPanel candidatePanel;

    // Container of the MNIST observations in most basic form
    protected double[][] targets;
    protected double[][] candidates;

    // Loss calculator
    protected ILoss l2 = new L2();

    /**
     * Constructor
     * @param outerPanel Outermost panel to reach the target-candidate panel
     */
    public TargetCandidatePanel(OuterPanel outerPanel) {
        this.outerPanel = outerPanel;

        this.targets = new MnistArrays(CountBasic.getMatrices(TARGET)).getInputs();
        this.candidates = new MnistArrays(CountBasic.getMatrices(CANDIDATE)).getInputs();

        double loss = l2.calculate(targets[targetIdx],candidates[candidateIdx]);

        targetPanel = new TitledImgPanel("Traget", TARGET, TARGET_IDX, loss);
        candidatePanel = new TitledImgPanel("Candidate", MatrixType.CANDIDATE, CANDIDATE_IDX);

        setLayout(new GridLayout(1,2));
        add(targetPanel);
        add(candidatePanel);
    }

    public void updateImage(int idx, MatrixType type) {
        TitledImgPanel panel = null;

        switch(type) {
            case TARGET:
                panel = targetPanel;
                targetIdx = idx;
                break;

            case CANDIDATE:
                panel = candidatePanel;
                candidateIdx = idx;

        }

        panel.setIndex(idx);
        panel.refresh();
    }

    /**
     * Updates the loss on the target panel--candidate panel does not have loss.
     */
    public void updateLoss() {
        double loss = l2.calculate(targets[targetIdx],candidates[candidateIdx]);
        targetPanel.setLoss(loss);

        targetPanel.refresh();
    }

    public void updateLoss(double loss) {
        targetPanel.setLoss(loss);
    }
    /**
     * Container of image inside a titled panel.
     */
    class TitledImgPanel extends JPanel {
        protected String title;
        protected int index;
        protected int label;
        protected double loss = -1;
        protected MatrixType type;

        // Actual images rendered here--it's necessary to embed the image panel inside
        // this panel since if we override the paint method, it overrides everything, including
        // the title info on the panel.
        protected JPanel imagePanel = new ImagePanel();

        /**
         * Constructor
         * @param title Panel title ("Target" or "Candidate")
         * @param type Type (TARGET or CANDIDATE)
         * @param index Image number
         * @param loss Loss value between the two images
         */
        public TitledImgPanel(String title, MatrixType type, int index, double loss) {
            this.title = title;
            this.type = type;

            setIndex(index);
            setLoss(loss);

            setLayout(new GridLayout(1,1));
            add(imagePanel);

            refresh();
        }

        /**
         * Overloaded constructor
         * @param title Panel title ("Target" or "Candidate")
         * @param type Type (TARGET or CANDIDATE)
         * @param index Image number
         */
        public TitledImgPanel(String title, MatrixType type, int index) {
            this(title, type, index, -1);
        }

        /**
         * Sets the image number.
         * @param index Image number
         */
        public void setIndex(int index) {
            this.index = index;
            this.label = CountBasic.getMatrices(type)[index].getLabel();
        }

        /**
         * Sets the loss.
         * @param loss Loss
         */
        public void setLoss(double loss) {
            this.loss = loss;
        }

        /**
         * Causes the panel to redraw itself.
         */
        public void refresh() {
            // This must be done in a worker thread to return control to Swing immediately.
            new Thread(() -> {
                String lossStr = String.format("%4.2f",loss);

                String text = title+" image: "+index+", label: "+label+(type== TARGET?", loss: "+lossStr:"");
                TitledBorder titledBorder = BorderFactory.createTitledBorder(text);
                titledBorder.setTitleFont(new Font("Arial", Font.PLAIN, 15));

                if(type == CANDIDATE) {
                    // See https://stackoverflow.com/questions/17850198/java-jpanel-two-borders-different-colors
                    boolean match = CountBasic.getMatrices(CANDIDATE)[candidatePanel.index].getLabel() ==
                            CountBasic.getMatrices(TARGET)[targetPanel.index].getLabel();
                    Color color = match ? Color.GREEN : Color.RED;
                    CompoundBorder compoundBorder = new CompoundBorder(
                            titledBorder,
                            BorderFactory.createMatteBorder(5, 5, 5, 5, color));
                    setBorder(compoundBorder);
                } else {
                    setBorder(titledBorder);
                }

                repaint();
            }).start();
        }

        /**
         * Contains the actual images and rendering of MNIST observations.
         */
        class ImagePanel extends JPanel {
            public ImagePanel() {
                setBackground(Color.WHITE);
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                final int WIDTH = 15;
                final int HEIGHT = 15;
                Random ran = new Random(0);

                MnistMatrix matrix = CountBasic.getMatrices(type)[index];

                for(int row=0; row < 28; row++) {
                    for(int col=0; col < 28; col++) {
                        // Uncomment this line if debugging.
//                Color color = new Color(ran.nextInt(256),ran.nextInt(256),ran.nextInt(256));

                        // Comment these lines if not debugging.
                        int rgb = 255 - matrix.getValue(row,col);
                        Color color = new Color(rgb,rgb,rgb);

                        // Render a cell.
                        g.setColor(color);
                        g.fillRect(col*WIDTH,row*HEIGHT,WIDTH,HEIGHT);

                        // Render a box around the cell
                        g.setColor(Color.BLUE);
                        g.drawRect(col*WIDTH,row*HEIGHT,WIDTH,HEIGHT);
                    }
                }
            }
        }
    }
}


/**
 * The other of two main panels.
 * Contains the navigation controls.
 */
class NavControls extends JPanel {
    Icon nextIcon = new ImageIcon("images/next-2.png");
    Icon prevIcon = new ImageIcon("images/previous-2.png");
    Icon searchIcon = new ImageIcon("images/search-3.png");
    Icon calculateIcon = new ImageIcon("images/calculate-1.png");

    protected OuterPanel outerPanel;
    protected TargetCandidatePanel tcp;

    protected GoPanel targetControls = new TargetNavControls(this);
    protected CandidateNavControls candidateControls = new CandidateNavControls(this);

    /**
     * Constructor
     * @param tcp Target control panel
     * @param outerPanel Outermost panel
     */
    public NavControls(TargetCandidatePanel tcp, OuterPanel outerPanel) {
        this.tcp = tcp;
        this.outerPanel = outerPanel;

        setLayout(new BorderLayout());
        add(targetControls,BorderLayout.WEST);
        add(candidateControls,BorderLayout.EAST);
    }

    /**
     * This panel has control to go to a specific image.
     */
    class GoPanel extends JPanel {
        protected NavControls navControls;
        protected JTextField imgNumField;
        protected JButton backButton;
        protected JButton forwardButton;
        protected JLabel imgLabel;

        /**
         * Constructor
         * @param navControls
         */
        public GoPanel(NavControls navControls) {
            this.navControls = navControls;

            setLayout(new FlowLayout(FlowLayout.RIGHT));

            backButton = new JButton(prevIcon);
            backButton.setMargin(new Insets(0,0,0,0));
            backButton.addActionListener(e -> {
                decrImageIdx();
                go();
            });

            forwardButton = new JButton(nextIcon);
            forwardButton.setMargin(new Insets(0,0,0,0));
            forwardButton.addActionListener(e -> {
                incrImageIdx();
                go();
            });

            imgLabel = new JLabel("Image:");
            imgLabel.setFont(new Font("Arial", Font.BOLD, 15));

            imgNumField = new JTextField();
            imgNumField.setPreferredSize(new Dimension(50,28));
            imgNumField.setText("10");
            imgNumField.addActionListener(e -> go());

            add(imgLabel);
            add(imgNumField);
            add(backButton);
            add(forwardButton);
        }

        /**
         * Causes skip to the image in the image number field.
         */
        public void go() {
            try {
                int idx = Integer.parseInt(imgNumField.getText());
                MatrixType which = (this instanceof TargetNavControls) ? TARGET : CANDIDATE;
                outerPanel.tcp.updateImage(idx,which);
            }
            catch(Exception ex) {}
        }

        /**
         * Updates the image number.
         * @param idx Image number
         */
        public void setImgIdx(int idx) {
            imgNumField.setText(idx+"");
        }

        /**
         * Decrements the image number.
         */
        public void decrImageIdx() {
            int currentNumber = Integer.parseInt(imgNumField.getText());
            if(currentNumber == 0)
                return;
            setImgIdx(currentNumber-1);
        }

        /**
         * Increments the image number.
         */
        public void incrImageIdx() {
            int currentNumber = Integer.parseInt(imgNumField.getText());
            if(currentNumber >= (60000-1))
                return;
            setImgIdx(currentNumber+1);
        }
    }

    /**
     * Contains components to navigate the candidates.
     */
    class CandidateNavControls extends JPanel {
        protected NavControls navControls;
        protected GoPanel goSubpanel = new GoPanel(this.navControls);

        public CandidateNavControls(NavControls navControls) {
            this.navControls = navControls;

            setLayout(new FlowLayout(FlowLayout.RIGHT));

            JButton doneButton = new JButton("Done");
            doneButton.addActionListener(e -> System.exit(0));

            JButton searchButton = new JButton(searchIcon);
            searchButton.setMargin(new Insets(0,5,0,5));
            searchButton.addActionListener(e -> {
                search();
            });

            JComboBox lossComboBox = new JComboBox(new String[]{"L2","Manhattan","Checkers","Cosine"});
            lossComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
            lossComboBox.setPreferredSize(new Dimension(80,25));

            lossComboBox.addActionListener(e -> {
                System.out.println(lossComboBox.getSelectedItem());
            });

            add(searchButton);
            add(goSubpanel);
            add(lossComboBox);
            add(doneButton);
        }

        protected void search() {
            new Thread(() -> {
                System.out.println("searching...");
                ILoss l2 = new L2();
                double[] target = tcp.targets[tcp.targetIdx];
                double minLoss = Double.MAX_VALUE;
                int minIdx = -1;
                for(int canidateno=0; canidateno < tcp.candidates.length; canidateno++) {
                    double[] canidate = tcp.candidates[canidateno];
                    double loss = l2.calculate(target,canidate);
                    if(loss < minLoss) {
                        minLoss = loss;
                        minIdx = canidateno;
                    }
                }
                System.out.println("search done! minIdx="+minIdx+" minLoss="+minLoss);
                goSubpanel.setImgIdx(minIdx);
                outerPanel.tcp.updateLoss(minLoss);
                outerPanel.tcp.updateImage(minIdx,CANDIDATE);                outerPanel.tcp.updateLoss();
            }).start();
        }
    }

    /**
     * Contains components to navigate the targets.
     */
    class TargetNavControls extends GoPanel {
        /**
         * Constructor
         * @param navControls Outer most container of navigation constrols, including this one.
         */
        public TargetNavControls(NavControls navControls) {
            super(navControls);

            JButton lossCalculateButton = new JButton(calculateIcon);
            lossCalculateButton.setMargin(new Insets(5,5,5,5));
            lossCalculateButton.setPreferredSize(new Dimension(25,30));
            lossCalculateButton.addActionListener(e -> {
                navControls.tcp.updateLoss();
            });

            // Replace the search button with loss button except on right
            for(Component component: this.getComponents())
                remove(component);

            add(imgLabel);
            add(imgNumField);
            add(backButton);
            add(forwardButton);
            add(lossCalculateButton);
        }
    }
}
