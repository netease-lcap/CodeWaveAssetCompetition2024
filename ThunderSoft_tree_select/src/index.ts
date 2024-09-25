export * from './components';
import * as logics from './logics';

const LIBRARY_NAME = 'codewave_component';
const UtilsLogics = {
  install: (Vue) => {
    Vue.prototype.$library = Vue.prototype.$library || {};
    Vue.prototype.$library[LIBRARY_NAME] = {
      ...logics,
    };
  },
};

export {
  UtilsLogics,
};
