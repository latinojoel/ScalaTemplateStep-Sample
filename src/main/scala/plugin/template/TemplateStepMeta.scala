package plugin.template

import org.pentaho.di.trans.step.StepMetaInterface
import org.pentaho.di.trans.step.BaseStepMeta
import org.pentaho.di.core.Const
import org.pentaho.di.core.row.RowMetaInterface
import org.pentaho.di.core.variables.VariableSpace
import org.pentaho.di.trans.step.StepMeta
import org.pentaho.di.core.row.ValueMetaInterface
import org.pentaho.di.core.row.ValueMeta
import org.w3c.dom.Node
import org.pentaho.di.core.Counter
import org.pentaho.di.core.database.DatabaseMeta
import org.pentaho.di.core.xml.XMLHandler
import org.pentaho.di.core.exception.KettleXMLException
import org.pentaho.di.core.CheckResultInterface
import org.pentaho.di.trans.TransMeta
import org.pentaho.di.core.CheckResult
import org.pentaho.di.trans.step.StepDialogInterface
import org.eclipse.swt.widgets.Shell
import org.pentaho.di.trans.Trans
import org.pentaho.di.trans.step.StepDataInterface
import org.pentaho.di.trans.step.StepInterface
import org.pentaho.di.repository.ObjectId
import org.pentaho.di.repository.Repository
import org.pentaho.di.core.exception.KettleException
import org.pentaho.di.i18n.BaseMessages
import java.util.List
import java.util.Map

class TemplateStepMeta extends BaseStepMeta with StepMetaInterface {
  def PKG: Class[TemplateStepMeta] = classOf[TemplateStepMeta] // for i18n purposes
  private var outputField:String = ""

  def TemplateStepMeta() {}

  def getOutputField(): String = { outputField }

  def setOutputField(outputField: String) = { this.outputField = outputField }

  override def getXML(): String = { "		<outputfield>" + getOutputField() + "</outputfield>" + Const.CR }

  override def getFields(r: RowMetaInterface, origin: String, info: Array[RowMetaInterface], nextStep: StepMeta, space: VariableSpace) = {
    // append the outputField to the output
    var v = new ValueMeta()
    v.setName(outputField)
    v.setType(ValueMetaInterface.TYPE_STRING)
    v.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH)
    v.setOrigin(origin)

    r.addValueMeta(v)
  }

  override def clone(): Object = super.clone()

  def loadXML(stepnode: Node, databases: List[DatabaseMeta], counters: Map[String, Counter]): Unit = {
    try {
      setOutputField(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outputfield")))
    } catch {
      case e: Exception => throw new KettleXMLException("Template Plugin Unable to read step info from XML node", e)
    }
  }

  def setDefault() = this.outputField = "template_outfield"

  override def check(remarks: List[CheckResultInterface], transmeta: TransMeta, stepMeta: StepMeta, prev: RowMetaInterface, input: Array[String], output: Array[String], info: RowMetaInterface): Unit = {
    if (input.length > 0) 
      remarks.add(new CheckResult(CheckResultInterface.TYPE_RESULT_OK, "Step is receiving info from other steps.", stepMeta))
    else
      remarks.add(new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, "No input received from other steps!", stepMeta))
  }

  def getDialog(shell: Shell, meta: StepMetaInterface, transMeta: TransMeta, name: String): StepDialogInterface =
    new TemplateStepDialog(shell, meta.asInstanceOf[BaseStepMeta], transMeta, name)

  def getStep(stepMeta: StepMeta, stepDataInterface: StepDataInterface, cnr: Int, transMeta: TransMeta, disp: Trans): StepInterface =
    new TemplateStep(stepMeta, stepDataInterface, cnr, transMeta, disp)

  def getStepData(): StepDataInterface = new TemplateStepData()

  def readRep(rep: Repository, id_step: ObjectId, databases: List[DatabaseMeta], counters: Map[String, Counter]) = {
    try {
      outputField = rep.getStepAttributeString(id_step, "outputfield") //$NON-NLS-1$
    } catch {
      case e: Exception => throw new KettleException(BaseMessages.getString(PKG, "TemplateStep.Exception.UnexpectedErrorInReadingStepInfo"), e)
    }
  }

  def saveRep(rep: Repository, id_transformation: ObjectId, id_step: ObjectId) = {
    try {
      rep.saveStepAttribute(id_transformation, id_step, "outputfield", outputField) //$NON-NLS-1$
    } catch {
      case e: Exception => throw new KettleException(BaseMessages.getString(PKG, "TemplateStep.Exception.UnableToSaveStepInfoToRepository") + id_step, e)
    }
  }

}