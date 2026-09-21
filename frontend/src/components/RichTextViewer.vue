<script setup>
import { onBeforeUnmount, watch } from 'vue'
import { EditorContent, useEditor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import { TaskItem, TaskList } from '@tiptap/extension-list'
import { FontSize } from '../extensions/FontSize'
import { TextColor } from '../extensions/TextColor'
import { Highlight } from '../extensions/Highlight'

const props = defineProps({ content: { type: String, default: '' } })
const emit = defineEmits(['update:content'])
const editor = useEditor({
  content: props.content,
  extensions: [StarterKit.configure({ heading: { levels: [1, 2, 3, 4] } }), TaskList, TaskItem.configure({ nested: true }), FontSize, TextColor, Highlight],
  editorProps: {
    attributes: { class: 'tiptap-reader-content', 'aria-label': '筆記內容；待辦事項可直接勾選', spellcheck: 'false' },
    handleKeyDown: (_view, event) => event.target?.type !== 'checkbox',
    handlePaste: () => true,
    handleDrop: () => true,
    handleTextInput: () => true,
    handleDOMEvents: { beforeinput: () => true }
  },
  onUpdate: ({ editor: instance }) => emit('update:content', instance.getHTML())
})
watch(() => props.content, value => editor.value?.commands.setContent(value || '', false))
onBeforeUnmount(() => editor.value?.destroy())
</script>

<template><EditorContent :editor="editor" /></template>
