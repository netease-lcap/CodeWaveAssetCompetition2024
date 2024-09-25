declare namespace extensions.codewave_component.viewComponents {
  const ViewComponent: typeof nasl.ui.ViewComponent, ViewComponentOptions: typeof nasl.ui.ViewComponentOptions;
  export class TreeSelectSn extends ViewComponent {
    constructor(options?: Partial<TreeSelectSnOptions>);
  }
  interface EventParam {
    isSwitchTab: boolean;
    activeTab: string;
    selected: any;
  }
  export class TreeSelectSnOptions extends ViewComponentOptions {
    /**
     * 选项卡名称
     * 选项卡名称
     */
    tabs: nasl.collection.List<any>;
    /**
     * 数据源值
     * 树对应的值
     */
    treeDataFromProps: nasl.collection.List<any>;
    /**
     * 懒加载数据源值
     * 懒加载绑定值
     */
    childrenFromProps: nasl.collection.List<any>;
    /**
     * 树展示字段
     * 树展示字段
     */
    label: nasl.core.String;
    /**
     * 叶子对应字段
     * 表示该节点是否是叶子节点，对应值为Boolean类型
     */
    leaf: nasl.core.String;
    /**
     * 头像url对应属性字段
     * 对应头像的url
     */
    avatar: nasl.core.String;
    /**
     * 过滤需要显示头像的数据属性
     * 用来过滤需要显示头像的数据
     */
    avatarKey: nasl.core.String;
    /**
     * 过滤需要显示头像的数据属性值
     * 用来过滤需要显示头像的数据
     */
    avatarValue: nasl.core.String;
    /**
     * 数据是否过滤
     * 添加按钮是否对数据进行过滤
     */
    isFilter: nasl.core.Boolean;
    /**
     * 数据过滤对应属性
     * 数据过滤对应属性
     */
    filterKey: nasl.core.String;
    /**
     * 数据过滤对应属性值
     * 数据过滤对应属性值
     */
    filterValue: nasl.core.String;
    /**
     * 懒加载触发获取子节点
     * 懒加载触发获取子节点
     */
    onHandleloadchildren: (event: {
      formData: EventParam;
    }) => void;
    /**
     * 选项卡切换事件
     * 选项卡切换事件
     */
    onHandletabchange: (event: {
      formData: EventParam;
    }) => void;
    /**
     * 数据提交
     * 数据提交
     */
    onHandleconfirm: (event: {
      formData: EventParam;
    }) => void;
  }
  export {};
}