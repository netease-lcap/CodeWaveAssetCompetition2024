<template>
  <el-rate
    v-if="!preview"
    v-model="currentValue"
    :max="currentMax"
    :showText="showText"
    :texts="texts"
    :allow-half="allowHalf"
    :disabled="readonly"
    @change="onChange"
  ></el-rate>
  <span v-else>{{ previewTxt }}</span>
</template>
<script>
export default {
  name: 'jxt-rate',
  props: {
    value: { type: Number, default: 0 },
    max: { type: Number, default: 5 },
    texts: {
      type: Array,
      default() {
        return ['极差', '失望', '一般', '满意', '惊喜'];
      },
    },
    showText: { type: Boolean, default: false },
    allowHalf: { type: Boolean, default: false },
    clearable: { type: Boolean, default: false },
    readonly: { type: Boolean, default: false },
    preview: { type: Boolean, default: false },
  },
  data() {
    return {
      currentValue: Number(this.value) > this.max ? this.max : Number(this.value),
      prevValue: 0,
    };
  },
  watch: {
    value(value, oldValue) {
      this.currentValue = Number(value);
    },
    currentValue(value, oldValue) {
      this.$emit(
        'change',
        {
          value,
          oldValue,
        },
        this
      );
    },
  },
  computed: {
    currentMax() {
      return this.max > 10000 ? 10000 : this.max;
    },
    previewTxt() {
      if (this.showText) {
        return this.texts[Number(this.currentValue) - 1] || '--';
      } else {
        return String(this.currentValue) || '--';
      }
    },
  },
  methods: {
    onChange(value) {
      if (this.readonly) {
        return;
      }
      if (this.clearable) {
        if (value === this.prevValue && this.prevValue !== 0) {
          this.currentValue = 0;
          this.prevValue = 0;
        } else {
          this.prevValue = value;
        }
      } else {
        this.prevValue = value;
      }
      this.$emit('update:value', this.currentValue, this);
    },
  },
};
</script>
<style module></style>
