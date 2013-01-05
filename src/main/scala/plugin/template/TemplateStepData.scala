package plugin.template

import org.pentaho.di.trans.step.BaseStepData
import org.pentaho.di.trans.step.StepDataInterface
import org.pentaho.di.core.row.RowMetaInterface

class TemplateStepData() extends BaseStepData() with StepDataInterface {
  var outputRowMeta: RowMetaInterface = _

  def TemplateStepData() {}
}