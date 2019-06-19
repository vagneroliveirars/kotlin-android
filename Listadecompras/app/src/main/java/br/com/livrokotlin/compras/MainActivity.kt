package br.com.livrokotlin.compras

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.toast
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val produtosAdapter = ProdutoAdapter(this)

        list_view_produtos.adapter = produtosAdapter

        btn_adicionar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        list_view_produtos.setOnItemLongClickListener { adapterView: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val item = produtosAdapter.getItem(position)
            deletarProduto(item.id)
            produtosAdapter.remove(item)
            toast("Item deletado com sucesso!")
            true
        }
    }

    override fun onResume() {
        super.onResume()

        val adapter = list_view_produtos.adapter as ProdutoAdapter

        database.use {
            // Efetuando uam consulta no banco de dados
            select("produtos").exec {
                // Criando o parser que montará o objeto produto
                val parser = rowParser {
                    // Colunas do banco de dados
                        id: Int, nome: String,
                        quantidade: Int,
                        valor: Double,
                        foto: ByteArray? ->

                    // Montagem do objeto Produto com as colunas do banco
                    Produto(id, nome, quantidade, valor, foto?.toBitmap())
                }

                // Criando a lista de produtos com dados do banco
                var listaProdutos = parseList(parser)

                // Limpando os dados da lista e carregando as novas informações
                adapter.clear()
                adapter.addAll(listaProdutos)

                val soma = listaProdutos.sumByDouble { produto -> produto.valor * produto.quantidade }
                val f = NumberFormat.getCurrencyInstance(Locale("pt", "br"))

                txt_total.text = "TOTAL: ${f.format(soma)}"
            }
        }
    }

    fun deletarProduto(idProduto: Int) {
        database.use {
            delete("produtos", "id = {id}", "id" to idProduto)
        }
    }

}
