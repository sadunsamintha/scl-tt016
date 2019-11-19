package com.sicpa.standard.gui.components.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton.Direction;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class SelectionTables<T> extends JPanel {

  protected static final long serialVersionUID = 1L;

  public static void main(final String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        SicpaLookAndFeelCusto.install();
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SelectionTables<Component> selectionTables = new SelectionTables<Component>(new String[] {
            "class.simpleName", "width"}, new String[] {"class", "width"});
        f.getContentPane().add(selectionTables);

        f.setSize(600, 300);

        for (Component comp : selectionTables.getComponents()) {
          selectionTables.addItem(comp);
        }

        f.setVisible(true);
      }
    });
  }

  protected BeanReaderJTable<T> tableLeft;
  protected BeanReaderJTable<T> tableRight;

  protected String[] properties;
  protected String[] titles;
  protected JButton buttonAdd;
  protected JButton buttonRemove;
  protected JButton buttonUp;
  protected JButton buttonDown;

  public SelectionTables(final String[] properties, final String[] titles) {
    this.properties = properties;
    this.titles = titles;
    initGUI();
  }

  protected void initGUI() {
    setLayout(new MigLayout("fill"));
    add(new JScrollPane(getTableLeft()), "spany 5,pushx,grow");
    add(new JScrollPane(getTableRight()), "spany 5,wrap,skip 1,pushx,grow");
    add(getButtonUp(), "grow,h 40! , w 40! , sg 1,wrap,pushy");
    add(getButtonDown(), "wrap,grow, sg 1,pushy");
    add(getButtonAdd(), "wrap,grow, sg 1,pushy");
    add(getButtonRemove(), "grow, sg 1,pushy");
  }

  @SuppressWarnings("serial")
  protected BeanReaderJTable<T> getTableLeft() {
    if (this.tableLeft == null) {
      this.tableLeft = new BeanReaderJTable<T>(this.properties, this.titles) {
        @Override
        public void setAutoCreateRowSorter(final boolean autoCreateRowSorter) {}
      };

      this.tableLeft.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

        @Override
        public void valueChanged(final ListSelectionEvent e) {
          tableLeftSelectionChanged();
        }
      });
    }
    return this.tableLeft;
  }

  protected void tableLeftSelectionChanged() {
    updateState();
  }

  @SuppressWarnings("serial")
  protected BeanReaderJTable<T> getTableRight() {
    if (this.tableRight == null) {
      this.tableRight = new BeanReaderJTable<T>(this.properties, this.titles) {
        @Override
        public void setAutoCreateRowSorter(final boolean autoCreateRowSorter) {}
      };
      this.tableRight.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(final ListSelectionEvent e) {
          tableRightSelectionChanged();
        }
      });
    }
    return this.tableRight;
  }

  protected void tableRightSelectionChanged() {
    updateState();
  }

  public JButton getButtonAdd() {
    if (this.buttonAdd == null) {
      this.buttonAdd = new DirectionButton(Direction.RIGHT);
      this.buttonAdd.setEnabled(false);
      this.buttonAdd.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          buttonAddActionPerformed();
        }
      });
    }
    return this.buttonAdd;
  }

  protected void buttonAddActionPerformed() {
    T[] tab = getTableLeft().getSelectedObjects();

    for (T t : tab) {
      addElementToSelection(t);
    }
  }

  protected void addElementToSelection(T t) {
    getTableRight().addRow(t);
    System.out.println(getSelectableItems());
    getTableLeft().removeRow(t);
    System.out.println(getSelectableItems());
  }

  public JButton getButtonDown() {
    if (this.buttonDown == null) {
      this.buttonDown = new DirectionButton(Direction.DOWN);
      this.buttonDown.setEnabled(false);
      this.buttonDown.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          buttonDownactionPerformed();
        }
      });
    }
    return this.buttonDown;
  }

  protected void buttonDownactionPerformed() {
    int[] tab = getTableRight().getSelectedRows();

    for (int i = tab.length - 1; i >= 0; i--) {
      getTableRight().getAllObjects().add(tab[i] + 2, getTableRight().getObjectAtRow(tab[i]));
      getTableRight().getAllObjects().remove(tab[i]);
    }

    getTableRight().getSelectionModel().setSelectionInterval(tab[0] + 1, tab[tab.length - 1] + 1);
  }

  public JButton getButtonRemove() {
    if (this.buttonRemove == null) {
      this.buttonRemove = new DirectionButton(Direction.LEFT);
      this.buttonRemove.setEnabled(false);
      this.buttonRemove.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          buttonRemoveActionPerformed();
        }
      });
    }
    return this.buttonRemove;
  }

  protected void buttonRemoveActionPerformed() {
    T[] tab = getTableRight().getSelectedObjects();

    for (T t : tab) {
      removeFromSelection(t);
    }
  }

  private void removeFromSelection(T t) {
    getTableLeft().addRow(t);
    getTableRight().removeRow(t);
  }

  public JButton getButtonUp() {
    if (this.buttonUp == null) {
      this.buttonUp = new DirectionButton(Direction.UP);
      this.buttonUp.setEnabled(false);
      this.buttonUp.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
          buttonUpActionPerformed();
        }
      });
    }
    return this.buttonUp;
  }

  protected void buttonUpActionPerformed() {
    int[] tab = getTableRight().getSelectedRows();

    for (int i : tab) {
      getTableRight().getAllObjects().add(i - 1, getTableRight().getObjectAtRow(i));
      getTableRight().getAllObjects().remove(i + 1);
    }

    getTableRight().getSelectionModel().setSelectionInterval(tab[0] - 1, tab[tab.length - 1] - 1);
  }

  public void addItem(final T t) {
    getTableLeft().addRow(t);
  }

  public void removeItem(final T t) {
    getTableLeft().removeRow(t);
    getTableRight().removeRow(t);
  }

  public List<T> getSelectedItems() {
    return getTableRight().getAllObjects();
  }

  public List<T> getSelectableItems() {
    return getTableLeft().getAllObjects();
  }

  public void setSelectedItems(List<T> selection) {
    for (T t : selection) {
      addElementToSelection(t);
    }
  }

  public void clearSelection() {
    List<T> selection = new ArrayList<>(getSelectedItems());
    for (T t : selection) {
      removeFromSelection(t);
    }
  }

  protected void updateState() {
    T tLeft = getTableLeft().getSelectedObject();
    T tRight = getTableRight().getSelectedObject();
    int[] tIndex = getTableRight().getSelectedRows();
    getButtonAdd().setEnabled(tLeft != null);
    getButtonRemove().setEnabled(tRight != null);
    getButtonUp().setEnabled(tRight != null);
    getButtonDown().setEnabled(tRight != null);

    if (tIndex.length > 0) {
      getButtonUp().setEnabled(tIndex[0] != 0);
      getButtonDown().setEnabled(tIndex[tIndex.length - 1] != getTableRight().getRowCount() - 1);
    }
  }

  @Override
  public void setEnabled(final boolean enabled) {
    super.setEnabled(enabled);

    this.buttonAdd.setEnabled(enabled);
    this.buttonRemove.setEnabled(enabled);
    this.buttonUp.setEnabled(enabled);
    this.buttonDown.setEnabled(enabled);
    this.tableLeft.setEnabled(enabled);
    this.tableRight.setEnabled(enabled);
  }

  public void setUPDownButtonsVisible(final boolean visible) {
    this.buttonUp.setVisible(visible);
    this.buttonDown.setVisible(visible);
  }
}
