/// <reference types="@nasl/types" />
namespace extensions.library_fdddf_diff.viewComponents {
  const { Component, Prop, ViewComponent, Slot, Method, Event, ViewComponentOptions } = nasl.ui;

  @ExtensionComponent({
    type: 'pc',
    ideusage: {
      idetype: 'element',
    }
  })
  @Component({
    title: '文本对比器',
    description: '文本对比器',
  })
  export class DiffViewer extends ViewComponent {
    constructor(options?: Partial<DiffViewerOptions>) {
      super();
    }
  }

  export class DiffViewerOptions extends ViewComponentOptions {
     @Prop({
      title: '文本一',
      description: '左侧文本',
      setter: {
        concept: 'InputSetter'
      }
    })
    one: nasl.core.String = '';

    @Prop({
      title: '文本二',
      description: '右侧文本',
      setter: {
        concept: 'InputSetter'
      }
    })
    other: nasl.core.String = '';

    @Prop({
      title: '文本对比维度类型',
      description: '文本对比维度类型',
      setter: {
        concept: 'EnumSelectSetter',
        options: [ { "title" : "行对比" }, { "title" : "字符对比" }, { "title" : "单词对比" } ],
      }
    })
    diffType: 'line' | 'char' | 'word' = 'line';

    @Prop({
      title: '删除内容背景色',
      description: '右侧相对左侧删除内容背景色',
      setter: {
        concept: 'InputSetter'
      }
    })
    delBackgroundColor: nasl.core.String = '#fbcaca';

    @Prop({
      title: '删除文本字体色',
      description: '右侧相对左侧删除文本字体色',
      setter: {
        concept: 'InputSetter'
      }
    })
    delColor: nasl.core.String = '#333';

    @Prop({
      title: '新增文本背景色',
      description: '右侧相对左侧新增文本背景色',
      setter: {
        concept: 'InputSetter'
      }
    })
    insBackgroundColor: nasl.core.String = '#d4fcbc';

    @Prop({
      title: '新增文本字体色',
      description: '右侧相对左侧新增文本字体色',
      setter: {
        concept: 'InputSetter'
      }
    })
    insColor: nasl.core.String = '#333';

  }
}