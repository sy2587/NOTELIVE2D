<script setup>
import { onBeforeUnmount, watch } from 'vue'
import { EditorContent, useEditor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import { TaskItem, TaskList } from '@tiptap/extension-list'

const props = defineProps({ content: { type: String, default: '' } })
const editor = useEditor({ content: props.content, editable: false, extensions: [StarterKit.configure({ heading: { levels: [1, 2, 3, 4] } }), TaskList, TaskItem.configure({ nested: true })], editorProps: { attributes: { class: 'tiptap-reader-content' } } })
watch(() => props.content, value => editor.value?.commands.setContent(value || '', false))
onBeforeUnmount(() => editor.value?.destroy())
</script>

<template><EditorContent :editor="editor" /></template>
