<script setup>
defineProps({ id: String, label: String, type: { type: String, default: 'text' }, modelValue: String, autocomplete: String, error: String, hint: String, minlength: Number })
defineEmits(['update:modelValue'])
</script>

<template><div class="form-field"><label :for="id">{{ label }}</label><input :id="id" :name="id" :type="type" :value="modelValue" :autocomplete="autocomplete" :minlength="minlength" required :aria-invalid="error ? 'true' : undefined" :aria-describedby="`${id}-help`" @input="$emit('update:modelValue', $event.target.value)"><p :id="`${id}-help`" :class="error ? 'field-error' : 'field-hint'">{{ error || hint || '' }}</p></div></template>
