package plugin.template

import org.pentaho.di.trans.step.BaseStep
import org.pentaho.di.trans.step.StepAttributesInterface
import org.pentaho.di.trans.step.StepDataInterface
import org.pentaho.di.trans.TransMeta
import org.pentaho.di.trans.Trans
import org.pentaho.di.trans.step.StepMeta
import org.pentaho.di.trans.step.StepMetaInterface
import org.pentaho.di.trans.step.StepInterface
import org.pentaho.di.core.row.RowDataUtil
import org.pentaho.di.core.Const

class TemplateStep(s: StepMeta, stepDataInterface: StepDataInterface, c: Int, t: TransMeta, dis: Trans) extends BaseStep(s: StepMeta, stepDataInterface: StepDataInterface, c: Int, t: TransMeta, dis: Trans) with StepInterface {
  var data: TemplateStepData = _
  var meta: TemplateStepMeta = _

  override def processRow(smi: StepMetaInterface, sdi: StepDataInterface): Boolean = {
    this.meta = smi.asInstanceOf[TemplateStepMeta]
    this.data = sdi.asInstanceOf[TemplateStepData]

    var r: Array[Object] = getRow()
    if (r == null) {
      setOutputDone()
      false;
    }

    if (first) {
      first = false
      this.data.outputRowMeta = super.getInputRowMeta()
      meta.getFields(data.outputRowMeta, getStepname(), null, null, this)
      logBasic("template step initialized successfully")
    }

    var outputRow: Array[Object] = RowDataUtil.addValueData(r, this.data.outputRowMeta.size() - 1, "dummy value")
    
    putRow(data.outputRowMeta, outputRow) // copy row to possible alternate rowset(s)

    if (checkFeedback(getLinesRead())) 
      logBasic("Linenr " + getLinesRead()) // Some basic logging
    
    
    true
  }

  override def init(smi: StepMetaInterface, sdi: StepDataInterface): Boolean = super.init(smi, sdi)

  override def dispose(smi: StepMetaInterface, sdi: StepDataInterface) = super.dispose(smi, sdi)

  // Run is were the action happens!
  def run() = {
    logBasic("Starting to run...");
    try {
      while (processRow(meta, data) && !isStopped()) {}
    } catch {
      case e: Exception => {
        logError("Unexpected error : " + e.toString());
        logError(Const.getStackTracker(e));
        setErrors(1);
        stopAll();
      }
    } finally {
      dispose(meta, data);
      logBasic("Finished, processing " + getLinesRead() + " rows");
      markStop();
    }
  }
}