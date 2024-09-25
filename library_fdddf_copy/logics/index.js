const $libraryName = 'library_fdddf_copy'

const UtilsLogics = {}
import copyText from './copyText'
// LOGIC IMPORTS

UtilsLogics.install = function (Vue, option = {}) {
    Vue.prototype.$library = Vue.prototype.$library || {}
    Vue.prototype.$library[`${$libraryName}`] = {}
    Vue.prototype.$library[`${$libraryName}`].copyText=copyText
    // LOGIC USE
}

export default UtilsLogics