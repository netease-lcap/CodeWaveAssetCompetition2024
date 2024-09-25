/// <reference types="@nasl/types" />
namespace extensions.codewave_component.viewComponents {
  const { Component, Prop, ViewComponent, Slot, Method, Event, ViewComponentOptions } = nasl.ui;

  @ExtensionComponent({
    type: 'pc',
    ideusage: {
      idetype: 'element',
    }
  })
  @Component({
    title: '带卡片的树形选择器',
    description: '带卡片的树形选择器',
  })
  export class TreeSelectSn extends ViewComponent {
    constructor(options?: Partial<TreeSelectSnOptions>) {
      super();
    }
  }

  interface EventParam {

    isSwitchTab: boolean;

    activeTab: string;

    selected: any;
  }


  export class TreeSelectSnOptions extends ViewComponentOptions {

    @Prop({
      title: '选项卡名称',
      description: '选项卡名称',
      sync: true,
    })
    tabs: nasl.collection.List<any>;

    @Prop({
      title: '数据源值',
      description: '树对应的值',
      sync: true,
    })
    treeDataFromProps: nasl.collection.List<any>;

    @Prop({
      title: '懒加载数据源值',
      description: '懒加载绑定值',
      sync: true,
    })
    childrenFromProps: nasl.collection.List<any>;


    @Prop({
      title: '树展示字段',
      description: '树展示字段',
      sync: true,
    })
    label: nasl.core.String;

    @Prop({
      title: '叶子对应字段',
      description: '表示该节点是否是叶子节点，对应值为Boolean类型',
      sync: true,
    })
    leaf: nasl.core.String;

    @Prop({
      title: '头像url对应属性字段',
      description: '对应头像的url',
      sync: true,
    })
    avatar: nasl.core.String;

    @Prop({
      title: '过滤需要显示头像的数据属性',
      description: '用来过滤需要显示头像的数据',
      sync: true,
    })
    avatarKey: nasl.core.String;

    @Prop({
      title: '过滤需要显示头像的数据属性值',
      description: '用来过滤需要显示头像的数据',
      sync: true,
    })
    avatarValue: nasl.core.String;

    @Prop({
      title: '数据是否过滤',
      description: '添加按钮是否对数据进行过滤',
      sync: true,
    })
    isFilter: nasl.core.Boolean;

    @Prop({
      title: '数据过滤对应属性',
      description: '数据过滤对应属性',
      sync: true,
    })
    filterKey: nasl.core.String;

    @Prop({
      title: '数据过滤对应属性值',
      description: '数据过滤对应属性值',
      sync: true,
    })
    filterValue: nasl.core.String;

    @Event({
      title: '懒加载触发获取子节点',
      description: '懒加载触发获取子节点',
    })
    onHandleloadchildren: (event: {
      formData : EventParam;
    }) => void;

    @Event({
      title: '选项卡切换事件',
      description: '选项卡切换事件',
    })
    onHandletabchange: (event: {
      formData : EventParam;
    }) => void;

    @Event({
      title: '数据提交',
      description: '数据提交',
    })
    onHandleconfirm: (event: {
      formData : EventParam;
    }) => void;

  }
}
