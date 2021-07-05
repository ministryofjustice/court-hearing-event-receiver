    {{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for web and worker containers
*/}}
{{- define "deployment.envs" }}
env:
  - name: HMPPS_AUTH_BASE_URL
    value: "{{ .Values.env.HMPPS_AUTH_BASE_URL }}"

{{- end -}}
