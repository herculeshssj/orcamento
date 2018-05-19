/**
  
  	Copyright (c) 2015 Hércules S. S. José

    Este arquivo é parte do programa Carnavalesco-Upload.
    

    Carnavalesco-Upload é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 2.1 da 

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, 

    mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a 
    
    qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública 
    
    Geral Menor GNU em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob o 

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/carnavalesco-upload 
    
    ou escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, 
    
    Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.

 */

package br.com.hslife.orcamento.controller;

import org.primefaces.event.FileUploadEvent;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@ManagedBean
@ViewScoped
public class UploadMB implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2060825880484859814L;
	
	private Integer idNoticia;
	private boolean exibirComponente = false;
	
	public void validaParametro() {
		try {
		if (idNoticia == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Parâmetro não informado!"));
		} else {
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Parâmetro informado! Valor: " + idNoticia));
			exibirComponente = true;
		}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Erro ao tratar o parâmetro: " + e.getMessage()));
		}
	}

	
	private String incluir150x100(String filename) {
		return filename.replace(".", "_150x100.");
	}
	
	
	/***
	 * Método adaptado para carregar arquivos e salvar no disco. 
	 * 
	 * Fonte: http://java.dzone.com/articles/how-upload-primefaces-under
	 * 
	 * @param event
	 */
	public void handleFileUpload(FileUploadEvent event) {
		
		// Verifica se o diretório da notícia existe
		File folder = new File("/usr/share/nginx/html/sistema/imagens/images/carnavalesco/geral/"+idNoticia);
		if (!folder.exists()) {
			folder.mkdir();
			File thumbnailsFolder = new File("/usr/share/nginx/html/sistema/imagens/images/carnavalesco/geral/"+idNoticia+"/thumbnails");
			thumbnailsFolder.mkdir();
		}
        
		File uploadedPhoto = new File("/usr/share/nginx/html/sistema/imagens/images/carnavalesco/geral/"+idNoticia+"/"+event.getFile().getFileName());
		File thumbnailPhoto = new File("/usr/share/nginx/html/sistema/imagens/images/carnavalesco/geral/"+idNoticia+"/thumbnails/"+this.incluir150x100(event.getFile().getFileName()));
		
		//File result = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("//WEB-INF//files//" + event.getFile().getFileName()));
		
		try {
			//FileOutputStream fileOutputStream = new FileOutputStream(result);
			FileOutputStream fosUploadedPhoto = new FileOutputStream(uploadedPhoto);
			FileOutputStream fosThumbnailPhoto = new FileOutputStream(thumbnailPhoto);
			
			//byte[] buffer = new byte[6124];
			byte[] bufUploadedPhoto = new byte[6124];
			byte[] bufThumbnailPhoto = new byte[6124];
			
			
			//int bulk;
			int bulkUploadedPhoto;
			int bulkThumbnailPhoto;
			
			//InputStream inputStream = event.getFile().getInputstream();
			InputStream isUploadedPhoto = event.getFile().getInputstream();
			InputStream isThumbnailPhoto = event.getFile().getInputstream();
			
			while (true) {
				bulkUploadedPhoto = isUploadedPhoto.read(bufUploadedPhoto);
				if (bulkUploadedPhoto < 0) {
					break;
				}
				fosUploadedPhoto.write(bufUploadedPhoto, 0, bulkUploadedPhoto);
				fosUploadedPhoto.flush();
			}
			fosUploadedPhoto.close();
			isUploadedPhoto.close();
			
			while (true) {
				bulkThumbnailPhoto = isThumbnailPhoto.read(bufThumbnailPhoto);
				if (bulkThumbnailPhoto < 0) {
					break;
				}
				fosThumbnailPhoto.write(bufThumbnailPhoto, 0, bulkThumbnailPhoto);
				fosThumbnailPhoto.flush();
			}
			fosThumbnailPhoto.close();
			isThumbnailPhoto.close();
			
			
			// Redimensiona a imagem
			UploadMB.redimenImagem("/usr/share/nginx/html/sistema/imagens/images/carnavalesco/geral/"+idNoticia+"/thumbnails/"+this.incluir150x100(event.getFile().getFileName()), 150, 100);
			
			/*
			while (true) {
				bulk = inputStream.read(buffer);
				if (bulk < 0) {
					break;
				}
				fileOutputStream.write(buffer, 0, bulk);
				fileOutputStream.flush();
			}*/
			
			
			//fileOutputStream.close();
			//inputStream.close();
			
			FacesMessage message = new FacesMessage("Sucesso", event.getFile().getFileName() + " carregado com sucesso!");
	        FacesContext.getCurrentInstance().addMessage(null, message);
			
		} catch (IOException e) {
			e.printStackTrace();
			FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The files were not uploaded!", "");
			FacesContext.getCurrentInstance().addMessage(null, error);
		}
    }

	/***
	 * Método estático para redimensionar imagens
	 * 
	 * Fonte: http://www.guj.com.br/java/229389-redimensionar-imagem
	 * 
	 * @param caminhoImg
	 * @param imgLargura
	 * @param imgAltura
	 * @throws IOException
	 */
    public static void redimenImagem(String caminhoImg, Integer imgLargura, Integer imgAltura) throws IOException {  
        BufferedImage imagem = ImageIO.read(new File(caminhoImg));  
          
        Double novaImgLargura = (double) imagem.getWidth();  
        Double novaImgAltura = (double) imagem.getHeight();  
  
        Double imgProporcao = null;  
        if (novaImgLargura >= imgLargura) {  
            imgProporcao = (novaImgAltura / novaImgLargura);  
            novaImgLargura = (double) imgLargura;  
            novaImgAltura = (novaImgLargura * imgProporcao);  
            while (novaImgAltura > imgAltura) {  
                novaImgLargura = (double) (--imgLargura);  
                novaImgAltura = (novaImgLargura * imgProporcao);  
            }  
        } else if (novaImgAltura >= imgAltura) {  
            imgProporcao = (novaImgLargura / novaImgAltura);  
            novaImgAltura = (double) imgAltura;  
            while (novaImgLargura > imgLargura) {  
                novaImgAltura = (double) (--imgAltura);  
                novaImgLargura = (novaImgAltura * imgProporcao);  
            }  
        }  
  
        BufferedImage novaImagem = new BufferedImage(novaImgLargura.intValue(), novaImgAltura.intValue(), imagem.getType());  
        Graphics g = novaImagem.getGraphics();  
        g.drawImage(imagem.getScaledInstance(novaImgLargura.intValue(), novaImgAltura.intValue(), 10000), 0, 0, null);  
        g.dispose();  
        ImageIO.write(novaImagem, "JPG", new File(caminhoImg));  
    }  
	
	public Integer getIdNoticia() {
		return idNoticia;
	}

	public void setIdNoticia(Integer idNoticia) {
		this.idNoticia = idNoticia;
	}

	public boolean isExibirComponente() {
		return exibirComponente;
	}	
}