package br.com.livrokotlin.compras

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cadastro.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.toast

class CadastroActivity : AppCompatActivity() {

    val COD_IMAGE = 101

    var imageBitMap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        btn_inserir.setOnClickListener {
            // Pegando os valores digitados pelo usuário
            val produto = txt_produto.text.toString()
            val qtd = txt_qtd.text.toString()
            val valor = txt_valor.text.toString()

            if (produto.isNotBlank() && qtd.isNotBlank() && valor.isNotBlank()) {
                database.use {
                    val idProduto = insert(
                        "Produtos",
                        "nome" to produto,
                        "quantidade" to qtd,
                        "valor" to valor.toDouble(),
                        "foto" to imageBitMap?.toByteArray()
                    )

                    if (idProduto != -1L) {
                        toast("Item inserido com sucesso")

                        txt_produto.text.clear()
                        txt_qtd.text.clear()
                        txt_valor.text.clear()
                    } else {
                        toast("Erro ao inserir no banco de dados")
                    }
                }
            } else {
                txt_produto.error = if (txt_produto.text.isBlank()) "Preencha o nome do produto" else null
                txt_qtd.error = if (txt_qtd.text.isEmpty()) "Preencha a quantidade" else null
                txt_valor.error = if (txt_valor.text.isEmpty()) "Preencha o valor" else null
            }
        }

        img_foto_produto.setOnClickListener {
            abrirGaleria()
        }
    }

    fun abrirGaleria() {
        //definindo a ação de conteúdo
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        //definindo filtro para imagens
        intent.type = "image/*"

        //inicializando a activity com resultado
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), COD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == COD_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                //lendo a uri com a imagem
                val inputStream = contentResolver.openInputStream(data.getData());

                //transformando o resultado em bitmap
                imageBitMap = BitmapFactory.decodeStream(inputStream)

                //Exibir a imagem no aplicativo
                img_foto_produto.setImageBitmap(imageBitMap)
            }
        }
    }
}
