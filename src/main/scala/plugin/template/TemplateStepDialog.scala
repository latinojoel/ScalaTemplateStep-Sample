package plugin.template

import org.pentaho.di.ui.trans.step.BaseStepDialog
import org.eclipse.swt.widgets.Shell
import org.pentaho.di.trans.TransMeta
import org.pentaho.di.trans.step.StepDialogInterface
import org.pentaho.di.trans.step.BaseStepMeta
import org.eclipse.swt.layout.FormData
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.SWT
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.layout.FormLayout
import org.pentaho.di.core.Const
import org.pentaho.di.i18n.BaseMessages
import org.eclipse.swt.layout.FormAttachment
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.ShellAdapter
import org.eclipse.swt.events.ShellEvent

class TemplateStepDialog(parent: Shell, in: BaseStepMeta, transMeta: TransMeta, sname: String) extends BaseStepDialog(parent: Shell, in: BaseStepMeta, transMeta: TransMeta, sname: String) with StepDialogInterface {
  def PKG: Class[TemplateStepDialog] = classOf[TemplateStepDialog]
  var input: TemplateStepMeta = new TemplateStepMeta()

  // output field name
  var wlValName: Label = _
  var wValName: Text = _
  var fdlValName, fdValName: FormData = _

  def TemplateStepDialog(parent: Shell, in: Object, transMeta: TransMeta, sname: String) = {
    this.input = in.asInstanceOf[TemplateStepMeta]
  }

  def open(): String = {
    var parent: Shell = getParent()
    var display: Display = parent.getDisplay()

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
    props.setLook(shell)
    setShellImage(shell, this.input)

    var lsMod: ModifyListener = new ModifyListener() {
      override def modifyText(e: ModifyEvent) = {
        input.setChanged()
      }
    }
    changed = input.hasChanged()

    var formLayout: FormLayout = new FormLayout()
    formLayout.marginWidth = Const.FORM_MARGIN
    formLayout.marginHeight = Const.FORM_MARGIN

    shell.setLayout(formLayout)
    shell.setText(BaseMessages.getString(PKG, "Template.Shell.Title"))

    var middle: Int = props.getMiddlePct()
    var margin: Int = Const.MARGIN

    // Stepname line
    wlStepname = new Label(shell, SWT.RIGHT)
    wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName"))
    props.setLook(wlStepname)
    fdlStepname = new FormData()
    fdlStepname.left = new FormAttachment(0, 0)
    fdlStepname.right = new FormAttachment(middle, -margin)
    fdlStepname.top = new FormAttachment(0, margin)
    wlStepname.setLayoutData(fdlStepname)

    wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER)
    wStepname.setText(stepname)
    props.setLook(wStepname)
    wStepname.addModifyListener(lsMod)
    fdStepname = new FormData()
    fdStepname.left = new FormAttachment(middle, 0)
    fdStepname.top = new FormAttachment(0, margin)
    fdStepname.right = new FormAttachment(100, 0)
    wStepname.setLayoutData(fdStepname)

    // output dummy value
    wlValName = new Label(shell, SWT.RIGHT)
    wlValName.setText(BaseMessages.getString(PKG, "Template.FieldName.Label"))
    props.setLook(wlValName)
    fdlValName = new FormData();
    fdlValName.left = new FormAttachment(0, 0)
    fdlValName.right = new FormAttachment(middle, -margin)
    fdlValName.top = new FormAttachment(wStepname, margin)
    wlValName.setLayoutData(fdlValName)

    wValName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wValName)
    wValName.addModifyListener(lsMod)
    fdValName = new FormData()
    fdValName.left = new FormAttachment(middle, 0)
    fdValName.right = new FormAttachment(100, 0)
    fdValName.top = new FormAttachment(wStepname, margin)
    wValName.setLayoutData(fdValName)

    // OK and cancel buttons
    wOK = new Button(shell, SWT.PUSH)
    wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"))
    wCancel = new Button(shell, SWT.PUSH)
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"))

    BaseStepDialog.positionBottomButtons(shell, Array[Button](wOK, wCancel), margin, wValName)

    // Add listeners
    lsCancel = new Listener() {
      def handleEvent(e: Event) = {
        cancel()
      }
    }
    lsOK = new Listener() {
      def handleEvent(e: Event) = {
        ok()
      }
    }

    wCancel.addListener(SWT.Selection, lsCancel)
    wOK.addListener(SWT.Selection, lsOK)

    lsDef = new SelectionAdapter() {
      override def widgetDefaultSelected(e: SelectionEvent) = {
        ok()
      }
    }

    wStepname.addSelectionListener(lsDef)
    wValName.addSelectionListener(lsDef)

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener(new ShellAdapter() {
      override def shellClosed(e: ShellEvent) ={
        cancel()
      }
    })

    // Set the shell size, based upon previous time...
    setSize()

    getData()
    input.setChanged(changed)

    shell.open()
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep()
    }
    stepname
  }
  
  // Read data and place it in the dialog
  def getData() = {
    wStepname.selectAll()
    wValName.setText(input.getOutputField())
  }

  def cancel() = {
    stepname = null
    input.setChanged(changed)
    dispose()
  }

  // let the plugin know about the entered data
  def ok() = {
    stepname = wStepname.getText() // return value
    input.setOutputField(wValName.getText())
    dispose()
  }
}