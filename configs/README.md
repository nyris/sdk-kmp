# How to generate encrypted base64 using gpg

```shell
    gpg -c -armor YOUR_FILE
```

# How to decrypt base64 string

```shell
  gpg -d --passphrase "YOUR_PASSPHRASE" --batch YOUR_FILE.asc > YOUR_FILE
```